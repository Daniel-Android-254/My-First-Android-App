package com.example.breathwatch.util

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.breathwatch.domain.model.HealthLog
import java.io.File
import java.io.FileWriter
import java.time.format.DateTimeFormatter

object CsvExporter {
    
    fun exportHealthLogs(
        context: Context,
        healthLogs: List<HealthLog>,
        onSuccess: (Intent) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val csvFile = createCsvFile(context, healthLogs)
            val shareIntent = createShareIntent(context, csvFile)
            onSuccess(shareIntent)
        } catch (e: Exception) {
            onError("Failed to export CSV: ${e.message}")
        }
    }
    
    private fun createCsvFile(context: Context, healthLogs: List<HealthLog>): File {
        val fileName = "breathwatch_health_logs_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), fileName)
        
        FileWriter(file).use { writer ->
            // Write CSV header
            writer.append("Date,Time,Overall Feeling,Symptoms,Notes,Latitude,Longitude,PM2.5,PM10,Temperature,Humidity,AQI\n")
            
            // Write data rows
            healthLogs.forEach { log ->
                val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                
                writer.append("${log.date.format(dateFormatter)},")
                writer.append("${log.date.format(timeFormatter)},")
                writer.append("${log.overallFeeling},")
                writer.append("\"${log.symptomsText}\",")
                writer.append("\"${log.notes ?: ""}\",")
                writer.append("${log.environmentalData?.latitude ?: ""},")
                writer.append("${log.environmentalData?.longitude ?: ""},")
                writer.append("${log.environmentalData?.pm25 ?: ""},")
                writer.append("${log.environmentalData?.pm10 ?: ""},")
                writer.append("${log.environmentalData?.temperature ?: ""},")
                writer.append("${log.environmentalData?.humidity ?: ""},")
                writer.append("${log.environmentalData?.aqi ?: ""}\n")
            }
        }
        
        return file
    }
    
    private fun createShareIntent(context: Context, file: File): Intent {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        
        return Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "BreathWatch Health Data Export")
            putExtra(Intent.EXTRA_TEXT, "Your health logs from BreathWatch app")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
