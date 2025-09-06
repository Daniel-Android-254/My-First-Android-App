package com.example.breathwatch.util

import android.content.Context
import androidx.work.*
import com.example.breathwatch.worker.AirQualitySyncWorker
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {
    
    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = PeriodicWorkRequestBuilder<AirQualitySyncWorker>(
            repeatInterval = Constants.DEFAULT_REFRESH_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 1,
            flexTimeIntervalUnit = TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(Constants.SYNC_WORKER_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.SYNC_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWorkRequest
        )
    }
    
    fun cancelPeriodicSync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(Constants.SYNC_WORKER_TAG)
    }
    
    fun scheduleOneTimeSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<AirQualitySyncWorker>()
            .setConstraints(constraints)
            .addTag("${Constants.SYNC_WORKER_TAG}_onetime")
            .build()

        WorkManager.getInstance(context).enqueue(syncWorkRequest)
    }
}
