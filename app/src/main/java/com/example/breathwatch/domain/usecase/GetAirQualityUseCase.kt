package com.example.breathwatch.domain.usecase

import com.example.breathwatch.data.local.entity.AirQualityEntity
import com.example.breathwatch.domain.model.AirQualityData
import com.example.breathwatch.domain.model.AqiCategory
import com.example.breathwatch.domain.repository.AirQualityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GetAirQualityUseCase @Inject constructor(
    private val airQualityRepository: AirQualityRepository
) {
    suspend fun execute(latitude: Double, longitude: Double): Result<AirQualityData> {
        return airQualityRepository.getAirQuality(latitude, longitude)
            .map { it.toDomainModel() }
    }
    
    fun observe(latitude: Double, longitude: Double): Flow<AirQualityData?> {
        return airQualityRepository.observeAirQuality(latitude, longitude)
            .map { it?.toDomainModel() }
    }
    
    fun observeCached(): Flow<AirQualityData?> {
        return airQualityRepository.observeCachedAirQuality()
            .map { it?.toDomainModel() }
    }
    
    private fun AirQualityEntity.toDomainModel(): AirQualityData {
        val aqiCategory = when (aqiCategory) {
            0 -> AqiCategory.GOOD
            1 -> AqiCategory.MODERATE
            2 -> AqiCategory.UNHEALTHY_FOR_SENSITIVE
            3 -> AqiCategory.UNHEALTHY
            4 -> AqiCategory.VERY_UNHEALTHY
            5 -> AqiCategory.HAZARDOUS
            else -> AqiCategory.UNKNOWN
        }
        
        return AirQualityData(
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            pm25 = pm25,
            pm10 = pm10,
            o3 = o3,
            no2 = no2,
            so2 = so2,
            co = co,
            aqi = aqi,
            aqiCategory = aqiCategory,
            lastUpdated = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastUpdated),
                ZoneId.systemDefault()
            ),
            isStale = isDataStale
        )
    }
}
