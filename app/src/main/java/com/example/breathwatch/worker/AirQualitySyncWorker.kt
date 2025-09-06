package com.example.breathwatch.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.breathwatch.BreathWatchApplication
import com.example.breathwatch.R
import com.example.breathwatch.domain.model.AqiCategory
import com.example.breathwatch.domain.usecase.GetAirQualityUseCase
import com.example.breathwatch.domain.usecase.GetWeatherUseCase
import com.example.breathwatch.util.Constants
import com.example.breathwatch.util.LocationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class AirQualitySyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getAirQualityUseCase: GetAirQualityUseCase,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val dataStore: DataStore<Preferences>
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Check if notifications are enabled
            val preferences = dataStore.data.first()
            val notificationsEnabled = preferences[booleanPreferencesKey("notifications_enabled")] ?: true
            
            if (!notificationsEnabled) {
                return Result.success()
            }

            // Get user location
            val userLocationName = preferences[stringPreferencesKey("user_location_name")]
            val userLat = preferences[stringPreferencesKey("user_location_lat")]?.toDoubleOrNull()
            val userLon = preferences[stringPreferencesKey("user_location_lon")]?.toDoubleOrNull()
            
            // Determine how to fetch data
            val airQualityResult = when {
                userLat != null && userLon != null -> {
                    // Use coordinates
                    getAirQualityUseCase.execute(userLat, userLon)
                }
                !userLocationName.isNullOrBlank() -> {
                    // Use location name - would need to implement geocoding
                    // For now, use default coordinates for Nairobi
                    getAirQualityUseCase.execute(-1.2921, 36.8219)
                }
                else -> {
                    // Use default location
                    getAirQualityUseCase.execute(-1.2921, 36.8219)
                }
            }

            // Fetch weather data as well
            val weatherResult = when {
                userLat != null && userLon != null -> {
                    getWeatherUseCase.execute(userLat, userLon)
                }
                else -> {
                    getWeatherUseCase.execute(-1.2921, 36.8219)
                }
            }

            // Check if we should send a notification
            airQualityResult.getOrNull()?.let { airQuality ->
                val aqiThreshold = preferences[intPreferencesKey("aqi_threshold")] ?: Constants.DEFAULT_AQI_THRESHOLD
                
                // Check if AQI exceeds threshold or is in unhealthy categories
                val shouldNotify = when {
                    airQuality.pm25 != null && airQuality.pm25 > aqiThreshold -> true
                    airQuality.aqiCategory in listOf(
                        AqiCategory.UNHEALTHY_FOR_SENSITIVE,
                        AqiCategory.UNHEALTHY,
                        AqiCategory.VERY_UNHEALTHY,
                        AqiCategory.HAZARDOUS
                    ) -> true
                    else -> false
                }

                if (shouldNotify) {
                    sendAirQualityNotification(airQuality.aqiCategoryText, airQuality.safetyAdvice)
                }
            }

            Result.success()
        } catch (e: Exception) {
            // Log error and retry
            Result.retry()
        }
    }

    private fun sendAirQualityNotification(aqiCategory: String, safetyAdvice: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(applicationContext, BreathWatchApplication.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification) // You'll need to add this icon
            .setContentTitle("Air Quality Alert")
            .setContentText("Air quality is $aqiCategory in your area")
            .setStyle(NotificationCompat.BigTextStyle().bigText(safetyAdvice))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(BreathWatchApplication.NOTIFICATION_ID, notification)
    }
}
