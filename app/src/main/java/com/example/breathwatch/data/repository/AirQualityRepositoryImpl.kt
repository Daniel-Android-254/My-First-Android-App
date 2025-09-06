package com.example.breathwatch.data.repository

import com.example.breathwatch.data.local.dao.AirQualityDao
import com.example.breathwatch.data.local.entity.AirQualityEntity
import com.example.breathwatch.data.remote.api.AirQualityApi
import com.example.breathwatch.data.remote.response.toAirQualityEntity
import com.example.breathwatch.domain.repository.AirQualityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AirQualityRepositoryImpl @Inject constructor(
    private val airQualityApi: AirQualityApi,
    private val airQualityDao: AirQualityDao
) : AirQualityRepository {

    override suspend fun getAirQuality(latitude: Double, longitude: Double) = try {
        // Try to get fresh data from API
        val response = airQualityApi.getAirQuality("$latitude,$longitude")
        val airQuality = response.toAirQualityEntity()
        
        // Save to local database
        airQualityDao.insertAirQuality(airQuality)
        
        // Also save as cached data
        val cachedAirQuality = airQuality.copy(isCached = true)
        airQualityDao.insertAirQuality(cachedAirQuality)
        
        Result.success(airQuality)
    } catch (e: Exception) {
        // If network fails, try to get from local database
        val localAirQuality = airQualityDao.getAirQualityById(
            AirQualityDao.getLocationId(latitude, longitude)
        )
        
        if (localAirQuality != null) {
            Result.success(localAirQuality)
        } else {
            Result.failure(e)
        }
    }

    override fun observeAirQuality(latitude: Double, longitude: Double): Flow<AirQualityEntity?> {
        return airQualityDao.observeAirQualityByLocation(latitude, longitude)
    }

    override suspend fun getAirQualityByLocationName(locationName: String) = try {
        val response = airQualityApi.getAirQualityByCity(locationName)
        val airQuality = response.toAirQualityEntity()
        
        // Save to local database
        airQualityDao.insertAirQuality(airQuality)
        
        // Also save as cached data
        val cachedAirQuality = airQuality.copy(isCached = true)
        airQualityDao.insertAirQuality(cachedAirQuality)
        
        Result.success(airQuality)
    } catch (e: Exception) {
        // If network fails, try to get from local database by name
        val localAirQuality = airQualityDao.getLatestAirQualityByLocationName(locationName)
        
        if (localAirQuality != null) {
            Result.success(localAirQuality)
        } else {
            Result.failure(e)
        }
    }

    override suspend fun saveAirQuality(airQuality: AirQualityEntity) {
        airQualityDao.insertAirQuality(airQuality)
    }

    override suspend fun getCachedAirQuality(): AirQualityEntity? {
        return airQualityDao.getAirQualityById("cached")
    }

    override fun observeCachedAirQuality(): Flow<AirQualityEntity?> {
        return airQualityDao.observeLatestCachedAirQuality()
    }

    override suspend fun getAirQualityHistory(
        latitude: Double,
        longitude: Double,
        days: Int
    ): Result<List<AirQualityEntity>> {
        // In a real app, this would fetch historical data from the API
        // For now, we'll return the latest data as a list with a single item
        return try {
            val current = getAirQuality(latitude, longitude).getOrThrow()
            Result.success(listOf(current))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAirQualityForDay(
        latitude: Double,
        longitude: Double,
        timestamp: Long
    ): Result<List<AirQualityEntity>> {
        // In a real app, this would fetch data for a specific day
        // For now, we'll just return the current data
        return try {
            val current = getAirQuality(latitude, longitude).getOrThrow()
            Result.success(listOf(current))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getCurrentDateTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
