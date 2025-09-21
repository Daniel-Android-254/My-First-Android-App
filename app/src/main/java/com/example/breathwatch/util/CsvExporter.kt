package com.example.breathwatch.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.breathwatch.domain.model.HealthLog
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

object CsvExporter {
    private const val DATE_PATTERN = "yyyy-MM-dd"
    private const val TIME_PATTERN = "HH:mm:ss"

    fun exportHealthLogs(
        context: Context,
        healthLogs: List<HealthLog>,
        onSuccess: (Intent) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            if (healthLogs.isEmpty()) {
                onError("No health logs to export")
                return
            }

            val csvFile = createCsvFile(context, healthLogs)
            if (csvFile.length() == 0L) {
                onError("Failed to write data to CSV file")
                return
            }

            val shareIntent = createShareIntent(context, csvFile)
            onSuccess(shareIntent)
        } catch (e: Exception) {
            onError("Failed to export CSV: ${e.message}")
            e.printStackTrace()
        }
    }
    
    private fun createCsvFile(context: Context, healthLogs: List<HealthLog>): File {
        val fileName = "breathwatch_health_logs_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        // Delete old export files
        cleanupOldExports(context)

        FileWriter(file).use { writer ->
            // Write CSV header with BOM for Excel compatibility
            writer.append('\ufeff')
            writer.append("Date,Time,Overall Feeling,Symptoms,Notes,Latitude,Longitude,PM2.5,PM10,Temperature,Humidity,AQI\n")
            
            // Write data rows
            healthLogs.forEach { log ->
                val dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
                val timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN)

                val row = buildString {
                    append("${log.date.format(dateFormatter)},")
                    append("${log.date.format(timeFormatter)},")
                    append("${sanitizeCsvField(log.overallFeeling)},")
                    append("\"${sanitizeCsvField(log.symptomsText)}\",")
                    append("\"${sanitizeCsvField(log.notes ?: "")}\",")
                    append("${log.environmentalData?.latitude ?: ""},")
                    append("${log.environmentalData?.longitude ?: ""},")
                    append("${log.environmentalData?.pm25 ?: ""},")
                    append("${log.environmentalData?.pm10 ?: ""},")
                    append("${log.environmentalData?.temperature ?: ""},")
                    append("${log.environmentalData?.humidity ?: ""},")
                    append("${log.environmentalData?.aqi ?: ""}\n")
                }
                writer.append(row)
            }
        }
        return file
    }
    
    private fun createShareIntent(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_SUBJECT, "BreathWatch Health Logs Export")
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    private fun cleanupOldExports(context: Context) {
        val exportsDir = context.getExternalFilesDir(null)
        val currentTime = System.currentTimeMillis()
        val oldFiles = exportsDir?.listFiles { file ->
            file.name.startsWith("breathwatch_health_logs_") &&
            file.name.endsWith(".csv") &&
            currentTime - file.lastModified() > 24 * 60 * 60 * 1000 // 24 hours
        }

        oldFiles?.forEach { it.delete() }
    }

    private fun sanitizeCsvField(field: String): String {
        return field.replace("\"", "\"\"")
            .replace("\n", " ")
            .replace("\r", " ")
            .trim()
    }
}
