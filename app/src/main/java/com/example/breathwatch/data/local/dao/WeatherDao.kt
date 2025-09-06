package com.example.breathwatch.data.local.dao

import androidx.room.*
import com.example.breathwatch.data.local.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    
    @Query("SELECT * FROM weather_data WHERE id = :id")
    suspend fun getWeatherById(id: String): WeatherEntity?
    
    @Query("""
        SELECT * FROM weather_data 
        WHERE latitude BETWEEN :lat - 0.1 AND :lat + 0.1 
        AND longitude BETWEEN :lon - 0.1 AND :lon + 0.1
        ORDER BY lastUpdated DESC
        LIMIT 1
    """)
    fun observeWeatherByLocation(lat: Double, lon: Double): Flow<WeatherEntity?>
    
    @Query("""
        SELECT * FROM weather_data 
        WHERE locationName LIKE '%' || :query || '%'
        ORDER BY lastUpdated DESC
        LIMIT 1
    """)
    suspend fun getLatestWeatherByLocationName(query: String): WeatherEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)
    
    @Update
    suspend fun updateWeather(weather: WeatherEntity)
    
    @Query("DELETE FROM weather_data WHERE lastUpdated < :timestamp")
    suspend fun deleteWeatherOlderThan(timestamp: Long)
    
    @Query("SELECT * FROM weather_data WHERE isCached = 1 ORDER BY lastUpdated DESC LIMIT 1")
    fun observeLatestCachedWeather(): Flow<WeatherEntity?>
    
    companion object {
        fun getLocationId(latitude: Double, longitude: Double): String {
            return "${latitude}_${longitude}"
        }
    }
}
