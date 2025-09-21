package com.example.breathwatch.util

import android.content.Context
import android.os.Build
import androidx.work.*
import com.example.breathwatch.worker.AirQualitySyncWorker
import com.example.breathwatch.worker.DataCleanupWorker
import java.util.concurrent.TimeUnit

object WorkManagerScheduler {
    
    private const val MIN_FLEX_INTERVAL = 15L // 15 minutes minimum flex time
    private const val BACKOFF_DELAY = WorkRequest.MIN_BACKOFF_MILLIS

    fun schedulePeriodicSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .setRequiresDeviceIdle(false)
            .setRequiresCharging(false)
            .setRequiresStorageNotLow(true)  // Ensure enough storage for caching
            .build()

        // Calculate optimal flex interval for battery efficiency
        val flexInterval = (Constants.DEFAULT_REFRESH_INTERVAL_HOURS * 60 / 4)
            .coerceAtLeast(MIN_FLEX_INTERVAL)

        val syncWorkRequest = PeriodicWorkRequestBuilder<AirQualitySyncWorker>(
            repeatInterval = Constants.DEFAULT_REFRESH_INTERVAL_HOURS,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = flexInterval,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .addTag(Constants.SYNC_WORKER_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                BACKOFF_DELAY,
                TimeUnit.MILLISECONDS
            )
            .setInitialDelay(5, TimeUnit.MINUTES)
            .build()

        // Use unique work to ensure only one sync chain is active
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.SYNC_WORKER_TAG,
            ExistingPeriodicWorkPolicy.UPDATE,
            syncWorkRequest
        )

        // Schedule cleanup work for old data
        scheduleCleanupWork(context)
    }

    private fun scheduleCleanupWork(context: Context) {
        val cleanupWorkRequest = PeriodicWorkRequestBuilder<DataCleanupWorker>(
            24, TimeUnit.HOURS
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresDeviceIdle(true)
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .addTag("${Constants.SYNC_WORKER_TAG}_cleanup")
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "${Constants.SYNC_WORKER_TAG}_cleanup",
            ExistingPeriodicWorkPolicy.KEEP,
            cleanupWorkRequest
        )
    }
    
    fun cancelPeriodicSync(context: Context) {
        WorkManager.getInstance(context).apply {
            cancelUniqueWork(Constants.SYNC_WORKER_TAG)
            cancelUniqueWork("${Constants.SYNC_WORKER_TAG}_cleanup")
        }
    }
    
    fun scheduleOneTimeSync(context: Context, expedited: Boolean = false) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val syncWorkRequest = OneTimeWorkRequestBuilder<AirQualitySyncWorker>()
            .setConstraints(constraints)
            .addTag("${Constants.SYNC_WORKER_TAG}_onetime")
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                BACKOFF_DELAY,
                TimeUnit.MILLISECONDS
            )
            .apply {
                if (expedited && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                }
            }
            .build()

        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "${Constants.SYNC_WORKER_TAG}_onetime",
                ExistingWorkPolicy.REPLACE,
                syncWorkRequest
            )
    }

    fun getWorkInfo(context: Context, callback: (List<WorkInfo>) -> Unit) {
        WorkManager.getInstance(context)
            .getWorkInfosByTagLiveData(Constants.SYNC_WORKER_TAG)
            .observeForever { workInfoList ->
                callback(workInfoList)
            }
    }

    fun isPeriodWorkScheduled(context: Context, callback: (Boolean) -> Unit) {
        WorkManager.getInstance(context)
            .getWorkInfosForUniqueWorkLiveData(Constants.SYNC_WORKER_TAG)
            .observeForever { workInfoList ->
                val isScheduled = workInfoList?.any {
                    it.state == WorkInfo.State.ENQUEUED ||
                    it.state == WorkInfo.State.RUNNING
                } ?: false
                callback(isScheduled)
            }
    }
}
