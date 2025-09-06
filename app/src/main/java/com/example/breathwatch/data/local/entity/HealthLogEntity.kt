package com.example.breathwatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "health_logs")
data class HealthLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val overallFeeling: Int, // 1-5 scale
    val hasCough: Boolean = false,
    val hasSoreThroat: Boolean = false,
    val hasWheeze: Boolean = false,
    val hasShortnessOfBreath: Boolean = false,
    val hasRunnyNose: Boolean = false,
    val notes: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val pm25: Double? = null,
    val pm10: Double? = null,
    val temperature: Double? = null,
    val humidity: Double? = null,
    val aqi: Int? = null
) {
    val localDate: LocalDate
        get() = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date),
            ZoneId.systemDefault()
        ).toLocalDate()
    
    val symptoms: List<String>
        get() = listOfNotNull(
            if (hasCough) "Cough" else null,
            if (hasSoreThroat) "Sore throat" else null,
            if (hasWheeze) "Wheezing" else null,
            if (hasShortnessOfBreath) "Shortness of breath" else null,
            if (hasRunnyNose) "Runny nose" else null
        )
}
