package com.example.breathwatch

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.Configuration
import com.example.breathwatch.util.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class BreathWatchApplication : Application() {
    
    @Inject
    lateinit var crashReporter: CrashReporter

    override fun onCreate() {
        super.onCreate()
        setupErrorHandling()
        setupNotificationChannels()
        setupWorkManager()
    }
    
    private fun setupErrorHandling() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            crashReporter.handleUncaughtException(thread, throwable)
            // Let the default handler run after we've logged the crash
            Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(thread, throwable)
        }
    }

    private fun setupNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Air Quality Alerts",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Notifications for air quality alerts and updates"
                    enableVibration(true)
                    setShowBadge(true)
                },
                NotificationChannel(
                    BACKGROUND_CHANNEL_ID,
                    "Background Updates",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notifications for background data updates"
                    setShowBadge(false)
                }
            )

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            channels.forEach { channel ->
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    private fun setupWorkManager() {
        // Initialize WorkManager with custom configuration
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .setWorkerFactory(WorkerFactory.getDefaultWorkerFactory())
            .build()

        WorkManager.initialize(this, config)

        // Schedule background work if needed
        WorkManagerScheduler.schedulePeriodicSync(this)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "air_quality_alerts"
        const val BACKGROUND_CHANNEL_ID = "background_updates"
        const val NOTIFICATION_ID = 1
    }
}
