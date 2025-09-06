package com.example.breathwatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@Entity(tableName = "air_quality_data")
data class AirQualityEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val pm25: Double?,
    val pm10: Double?,
    val o3: Double?,
    val no2: Double?,
    val so2: Double?,
    val co: Double?,
    val aqi: Int?,
    val aqiCategory: Int,
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
