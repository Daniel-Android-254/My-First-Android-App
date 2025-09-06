package com.example.breathwatch.domain.repository

import com.example.breathwatch.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): Result<WeatherEntity>
    fun observeWeather(latitude: Double, longitude: Double): Flow<WeatherEntity?>
    suspend fun getWeatherByLocationName(locationName: String): Result<WeatherEntity>
    suspend fun saveWeather(weather: WeatherEntity)
    suspend fun getCachedWeather(): WeatherEntity?
    fun observeCachedWeather(): Flow<WeatherEntity?>
    
    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        days: Int = 3
    ): Result<List<WeatherEntity>>
}
