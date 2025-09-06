package com.example.breathwatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "weather_data")
data class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val conditionText: String,
    val conditionIcon: String?,
    val humidity: Int,
    val windSpeedKph: Double,
    val windDirection: String,
    val precipitationMm: Double,
    val pressureMb: Double,
    val visibilityKm: Double,
    val cloudCover: Int,
    val uvIndex: Double,
    val lastUpdated: Long,
    val isCached: Boolean = false
) {
    val lastUpdatedDateTime: LocalDateTime
        get() = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(lastUpdated),
            ZoneId.systemDefault()
        )
    
    val isDataStale: Boolean
        get() = System.currentTimeMillis() - lastUpdated > 6 * 60 * 60 * 1000 // 6 hours
}
