package com.example.breathwatch.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.breathwatch.data.local.dao.AirQualityDao
import com.example.breathwatch.data.local.dao.WeatherDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DataCleanupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val airQualityDao: AirQualityDao,
    private val weatherDao: WeatherDao
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Clean up old air quality data
            val airQualityCleanupCount = airQualityDao.deleteOldData(
                olderThan = System.currentTimeMillis() - OLD_DATA_THRESHOLD
            )

            // Clean up old weather data
            val weatherCleanupCount = weatherDao.deleteOldData(
                olderThan = System.currentTimeMillis() - OLD_DATA_THRESHOLD
            )

            // Log cleanup results
            if (airQualityCleanupCount > 0 || weatherCleanupCount > 0) {
                android.util.Log.i(
                    "DataCleanupWorker",
                    "Cleaned up $airQualityCleanupCount air quality records and $weatherCleanupCount weather records"
                )
            }

            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("DataCleanupWorker", "Error cleaning up old data", e)
            Result.failure()
        }
    }

    companion object {
        // Keep data for 7 days
        private val OLD_DATA_THRESHOLD = TimeUnit.DAYS.toMillis(7)
    }
}
