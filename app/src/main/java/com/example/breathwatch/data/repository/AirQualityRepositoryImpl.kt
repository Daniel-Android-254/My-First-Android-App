package com.example.breathwatch.data.repository

import com.example.breathwatch.data.local.dao.AirQualityDao
import com.example.breathwatch.data.local.entity.AirQualityEntity
import com.example.breathwatch.data.remote.api.AirQualityApi
import com.example.breathwatch.data.remote.response.toAirQualityEntity
import com.example.breathwatch.domain.repository.AirQualityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirQualityRepositoryImpl @Inject constructor(
    private val airQualityApi: AirQualityApi,
    private val airQualityDao: AirQualityDao,
    private val cacheStrategy: CacheStrategy
) : AirQualityRepository, BaseRepository<AirQualityEntity> {

    override suspend fun getAirQuality(latitude: Double, longitude: Double) = try {
        require(latitude in -90.0..90.0) { "Invalid latitude: $latitude" }
        require(longitude in -180.0..180.0) { "Invalid longitude: $longitude" }

        cacheStrategy.cachingStrategy(
            dbQuery = { airQualityDao.observeAirQualityByLocation(latitude, longitude) },
            networkCall = {
                Result.wrap {
                    airQualityApi.getAirQuality("$latitude,$longitude")
                        .toAirQualityEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { airQualityDao.insertAirQuality(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, MAX_AGE)
            }
        ).first()
    } catch (e: IllegalArgumentException) {
        Result.Error(e)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun observeAirQuality(latitude: Double, longitude: Double): Flow<Result<AirQualityEntity>> {
        return cacheStrategy.cachingStrategy(
            dbQuery = { airQualityDao.observeAirQualityByLocation(latitude, longitude) },
            networkCall = {
                Result.wrap {
                    airQualityApi.getAirQuality("$latitude,$longitude")
                        .toAirQualityEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { airQualityDao.insertAirQuality(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, MAX_AGE)
            }
        )
    }

    override suspend fun getAirQualityByLocationName(locationName: String) = try {
        require(locationName.isNotBlank()) { "Location name cannot be empty" }

        cacheStrategy.cachingStrategy(
            dbQuery = { airQualityDao.observeLatestAirQualityByLocationName(locationName) },
            networkCall = {
                Result.wrap {
                    airQualityApi.getAirQualityByCity(locationName)
                        .toAirQualityEntity()
                        .copy(timestamp = System.currentTimeMillis())
                }
            },
            saveCallResult = { airQualityDao.insertAirQuality(it) },
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
        val cachedLocations = airQualityDao.getAllLocations()
        cachedLocations.forEach { location ->
            cacheStrategy.refreshData {
                val response = airQualityApi.getAirQuality(location)
                val entity = response.toAirQualityEntity()
                    .copy(timestamp = System.currentTimeMillis())
                airQualityDao.insertAirQuality(entity)
            }
        }
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun observe(): Flow<Result<List<AirQualityEntity>>> {
        return cacheStrategy.cachingStrategy(
            dbQuery = { airQualityDao.observeAllAirQuality() },
            networkCall = { refresh() },
            saveCallResult = { /* Already saved in refresh() */ },
            shouldFetch = { cached ->
                cached?.any { isDataStale(it.timestamp, MAX_AGE) } ?: true
            }
        )
    }

    override suspend fun get(): Result<List<AirQualityEntity>> = observe().first()

    override suspend fun clear() {
        airQualityDao.deleteAll()
    }

    companion object {
        private const val MAX_AGE = 30 * 60 * 1000L // 30 minutes
    }
}
