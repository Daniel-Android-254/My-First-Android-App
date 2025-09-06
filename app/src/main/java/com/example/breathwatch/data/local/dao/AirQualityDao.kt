package com.example.breathwatch.data.local.dao

import androidx.room.*
import com.example.breathwatch.data.local.entity.AirQualityEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset

@Dao
interface AirQualityDao {
    
    @Query("SELECT * FROM air_quality_data WHERE id = :id")
    suspend fun getAirQualityById(id: String): AirQualityEntity?
    
    @Query("""
        SELECT * FROM air_quality_data 
        WHERE latitude BETWEEN :lat - 0.1 AND :lat + 0.1 
        AND longitude BETWEEN :lon - 0.1 AND :lon + 0.1
        ORDER BY lastUpdated DESC
        LIMIT 1
    """)
    fun observeAirQualityByLocation(lat: Double, lon: Double): Flow<AirQualityEntity?>
    
    @Query("""
        SELECT * FROM air_quality_data 
        WHERE locationName LIKE '%' || :query || '%'
        ORDER BY lastUpdated DESC
        LIMIT 1
    """)
    suspend fun getLatestAirQualityByLocationName(query: String): AirQualityEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAirQuality(airQuality: AirQualityEntity)
    
    @Update
    suspend fun updateAirQuality(airQuality: AirQualityEntity)
    
    @Query("DELETE FROM air_quality_data WHERE lastUpdated < :timestamp")
    suspend fun deleteAirQualityOlderThan(timestamp: Long)
    
    @Query("SELECT * FROM air_quality_data WHERE isCached = 1 ORDER BY lastUpdated DESC LIMIT 1")
    fun observeLatestCachedAirQuality(): Flow<AirQualityEntity?>
    
    @Query("""
        SELECT * FROM air_quality_data 
        WHERE lastUpdated >= :startOfDay AND lastUpdated < :endOfDay
        ORDER BY lastUpdated ASC
    """)
    fun getAirQualityForDay(startOfDay: Long, endOfDay: Long): Flow<List<AirQualityEntity>>
    
    companion object {
        fun getLocationId(latitude: Double, longitude: Double): String {
            return "${latitude}_${longitude}"
        }
    }
}
