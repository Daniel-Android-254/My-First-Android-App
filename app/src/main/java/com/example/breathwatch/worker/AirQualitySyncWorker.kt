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
                    getAirQualityUseCase(GetAirQualityUseCase.Params(userLat, userLon))
                }
                userLocationName != null -> {
                    getAirQualityUseCase(GetAirQualityUseCase.Params(locationName = userLocationName))
                }
                else -> {
                    // Try to get current location as fallback
                    val location = LocationHelper.getLastKnownLocation(applicationContext)
                    if (location != null) {
                        getAirQualityUseCase(GetAirQualityUseCase.Params(location.latitude, location.longitude))
                    } else {
                        return Result.retry()
                    }
                }
            }

            airQualityResult.fold(
                onSuccess = { airQuality ->
                    // Check AQI threshold for notification
                    val aqiThreshold = preferences[intPreferencesKey("aqi_threshold")] ?: 100
                    if (airQuality.aqi > aqiThreshold) {
                        val notification = NotificationCompat.Builder(applicationContext, BreathWatchApplication.NOTIFICATION_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_notification)
                            .setContentTitle("Air Quality Alert")
                            .setContentText("Current AQI is ${airQuality.aqi} (${AqiCategory.fromAqi(airQuality.aqi).description})")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setAutoCancel(true)
                            .build()

                        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(BreathWatchApplication.NOTIFICATION_ID, notification)
                    }
                    Result.success()
                },
                onFailure = { error ->
                    // Only retry on network-related errors
                    if (error is java.io.IOException) {
                        Result.retry()
                    } else {
                        Result.failure()
                    }
                }
            )
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
