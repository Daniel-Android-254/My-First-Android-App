package com.example.breathwatch.domain.usecase

import com.example.breathwatch.data.local.entity.WeatherEntity
import com.example.breathwatch.domain.model.WeatherData
import com.example.breathwatch.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(latitude: Double, longitude: Double): Result<WeatherData> {
        return weatherRepository.getWeather(latitude, longitude)
            .map { it.toDomainModel() }
    }
    
    fun observe(latitude: Double, longitude: Double): Flow<WeatherData?> {
        return weatherRepository.observeWeather(latitude, longitude)
            .map { it?.toDomainModel() }
    }
    
    fun observeCached(): Flow<WeatherData?> {
        return weatherRepository.observeCachedWeather()
            .map { it?.toDomainModel() }
    }
    
    private fun WeatherEntity.toDomainModel(): WeatherData {
        return WeatherData(
            latitude = latitude,
            longitude = longitude,
            locationName = locationName,
            temperatureCelsius = temperatureCelsius,
            temperatureFahrenheit = temperatureFahrenheit,
            conditionText = conditionText,
            conditionIcon = conditionIcon,
            humidity = humidity,
            windSpeedKph = windSpeedKph,
            windDirection = windDirection,
            precipitationMm = precipitationMm,
            pressureMb = pressureMb,
            visibilityKm = visibilityKm,
            cloudCover = cloudCover,
            uvIndex = uvIndex,
            lastUpdated = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(lastUpdated),
                ZoneId.systemDefault()
            ),
            isStale = isDataStale
        )
    }
}
