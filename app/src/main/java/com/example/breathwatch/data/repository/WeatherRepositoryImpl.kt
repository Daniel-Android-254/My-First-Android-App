package com.example.breathwatch.data.repository

import com.example.breathwatch.data.local.dao.WeatherDao
import com.example.breathwatch.data.local.entity.WeatherEntity
import com.example.breathwatch.data.remote.api.WeatherApi
import com.example.breathwatch.data.remote.response.toWeatherEntity
import com.example.breathwatch.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherApi: WeatherApi,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double) = try {
        // Try to get fresh data from API
        val response = weatherApi.getWeather("$latitude,$longitude")
        val weather = response.toWeatherEntity()
        
        // Save to local database
        weatherDao.insertWeather(weather)
        
        // Also save as cached data
        val cachedWeather = weather.copy(isCached = true)
        weatherDao.insertWeather(cachedWeather)
        
        Result.success(weather)
    } catch (e: Exception) {
        // If network fails, try to get from local database
        val localWeather = weatherDao.getWeatherById(
            WeatherDao.getLocationId(latitude, longitude)
        )
        
        if (localWeather != null) {
            Result.success(localWeather)
        } else {
            Result.failure(e)
        }
    }

    override fun observeWeather(latitude: Double, longitude: Double): Flow<WeatherEntity?> {
        return weatherDao.observeWeatherByLocation(latitude, longitude)
    }

    override suspend fun getWeatherByLocationName(locationName: String) = try {
        val response = weatherApi.getWeather(locationName)
        val weather = response.toWeatherEntity()
        
        // Save to local database
        weatherDao.insertWeather(weather)
        
        // Also save as cached data
        val cachedWeather = weather.copy(isCached = true)
        weatherDao.insertWeather(cachedWeather)
        
        Result.success(weather)
    } catch (e: Exception) {
        // If network fails, try to get from local database by name
        val localWeather = weatherDao.getLatestWeatherByLocationName(locationName)
        
        if (localWeather != null) {
            Result.success(localWeather)
        } else {
            Result.failure(e)
        }
    }

    override suspend fun saveWeather(weather: WeatherEntity) {
        weatherDao.insertWeather(weather)
    }

    override suspend fun getCachedWeather(): WeatherEntity? {
        return weatherDao.getWeatherById("cached")
    }

    override fun observeCachedWeather(): Flow<WeatherEntity?> {
        return weatherDao.observeLatestCachedWeather()
    }

    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        days: Int
    ): Result<List<WeatherEntity>> {
        return try {
            val response = weatherApi.getWeatherForecast("$latitude,$longitude", numOfDays = days)
            val weather = response.toWeatherEntity()
            
            // Save to local database
            weatherDao.insertWeather(weather)
            
            // For now, return single item as forecast
            // In a real implementation, you'd parse multiple days from the response
            Result.success(listOf(weather))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
