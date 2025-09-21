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
    private val weatherDao: WeatherDao,
    private val cacheStrategy: CacheStrategy
) : WeatherRepository, BaseRepository<WeatherEntity> {

    override suspend fun getWeather(latitude: Double, longitude: Double) = try {
        require(latitude in -90.0..90.0) { "Invalid latitude: $latitude" }
        require(longitude in -180.0..180.0) { "Invalid longitude: $longitude" }

        cacheStrategy.cachingStrategy(
            dbQuery = { weatherDao.observeWeatherByLocation(latitude, longitude) },
            networkCall = {
                Result.wrap {
                    weatherApi.getWeather("$latitude,$longitude")
                        .toWeatherEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { weatherDao.insertWeather(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, MAX_AGE)
            }
        ).first()
    } catch (e: IllegalArgumentException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun observeWeather(latitude: Double, longitude: Double): Flow<Result<WeatherEntity>> {
        return cacheStrategy.cachingStrategy(
            dbQuery = { weatherDao.observeWeatherByLocation(latitude, longitude) },
            networkCall = {
                Result.wrap {
                    weatherApi.getWeather("$latitude,$longitude")
                        .toWeatherEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { weatherDao.insertWeather(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, MAX_AGE)
            }
        )
    }

    override suspend fun getWeatherForecast(latitude: Double, longitude: Double) = try {
        require(latitude in -90.0..90.0) { "Invalid latitude: $latitude" }
        require(longitude in -180.0..180.0) { "Invalid longitude: $longitude" }

        cacheStrategy.cachingStrategy(
            dbQuery = { weatherDao.observeForecastByLocation(latitude, longitude) },
            networkCall = {
                Result.wrap {
                    weatherApi.getWeatherForecast("$latitude,$longitude")
                        .toWeatherEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { weatherDao.insertWeather(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, FORECAST_MAX_AGE)
            }
        ).first()
    } catch (e: IllegalArgumentException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun getWeatherByLocationName(locationName: String) = try {
        require(locationName.isNotBlank()) { "Location name cannot be empty" }

        cacheStrategy.cachingStrategy(
            dbQuery = { weatherDao.observeLatestWeatherByLocationName(locationName) },
            networkCall = {
                Result.wrap {
                    weatherApi.getWeather(locationName)
                        .toWeatherEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { weatherDao.insertWeather(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, MAX_AGE)
            }
        ).first()
    } catch (e: IllegalArgumentException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun refresh() = try {
        // Refresh all cached locations
        val cachedLocations = weatherDao.getAllLocations()
        cachedLocations.forEach { location ->
            cacheStrategy.refreshData {
                val response = weatherApi.getWeather(location)
                val entity = response.toWeatherEntity()
                    .copy(timestamp = System.currentTimeMillis())
                weatherDao.insertWeather(entity)
            }
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun observe(): Flow<Result<List<WeatherEntity>>> {
        return cacheStrategy.cachingStrategy(
            dbQuery = { weatherDao.observeAllWeather() },
            networkCall = { refresh() },
            saveCallResult = { /* Already saved in refresh() */ },
            shouldFetch = { cached ->
                cached?.any { isDataStale(it.timestamp, MAX_AGE) } ?: true
            }
        )
    }

    override suspend fun get(): Result<List<WeatherEntity>> = observe().first()

    override suspend fun clear() {
        weatherDao.deleteAll()
    }

    companion object {
        private const val MAX_AGE = 30 * 60 * 1000L // 30 minutes
        private const val FORECAST_MAX_AGE = 3 * 60 * 60 * 1000L // 3 hours
    }
}
