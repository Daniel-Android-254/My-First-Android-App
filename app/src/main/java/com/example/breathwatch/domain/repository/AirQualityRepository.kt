package com.example.breathwatch.domain.repository

import com.example.breathwatch.data.local.entity.AirQualityEntity
import kotlinx.coroutines.flow.Flow

interface AirQualityRepository {
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityEntity>
    fun observeAirQuality(latitude: Double, longitude: Double): Flow<AirQualityEntity?>
    suspend fun getAirQualityByLocationName(locationName: String): Result<AirQualityEntity>
    suspend fun saveAirQuality(airQuality: AirQualityEntity)
    suspend fun getCachedAirQuality(): AirQualityEntity?
    fun observeCachedAirQuality(): Flow<AirQualityEntity?>
    suspend fun getAirQualityHistory(
        latitude: Double,
        longitude: Double,
        days: Int = 7
    ): Result<List<AirQualityEntity>>
    
    suspend fun getAirQualityForDay(
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): Result<List<AirQualityEntity>>
}
