package com.example.breathwatch.util

import android.content.Context
import android.os.Build
import com.example.breathwatch.BuildConfig
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class CrashReporter @Inject constructor(
    private val context: Context
) {
    private val crashLogsDir: File
        get() = File(context.getExternalFilesDir(null), "crash_logs").apply {
            if (!exists()) mkdirs()
        }

    fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val filename = "crash_$timestamp.txt"
            val reportFile = File(crashLogsDir, filename)

            FileWriter(reportFile).use { writer ->
                writer.append("=== Crash Report ===\n\n")
                writer.append("Time: $timestamp\n")
                writer.append("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})\n")
                writer.append("Device: ${Build.MANUFACTURER} ${Build.MODEL}\n")
                writer.append("Android Version: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})\n")
                writer.append("Thread: ${thread.name}\n\n")
                writer.append("=== Exception ===\n\n")
                writer.append("${throwable.javaClass.name}: ${throwable.message}\n\n")
                throwable.stackTrace.forEach { element ->
                    writer.append("    at $element\n")
                }
            }

            // Clean up old crash logs
            cleanupOldCrashLogs()
        } catch (e: Exception) {
            android.util.Log.e("CrashReporter", "Error saving crash report", e)
        }
    }

    private fun cleanupOldCrashLogs() {
        try {
            val maxAge = System.currentTimeMillis() - MAX_LOG_AGE
            crashLogsDir.listFiles()
                ?.filter { it.lastModified() < maxAge }
                ?.forEach { it.delete() }
        } catch (e: Exception) {
            android.util.Log.e("CrashReporter", "Error cleaning up old crash logs", e)
        }
    }

    companion object {
        private const val MAX_LOG_AGE = 7 * 24 * 60 * 60 * 1000L // 7 days in milliseconds
    }
}
