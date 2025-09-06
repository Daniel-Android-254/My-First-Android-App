package com.example.breathwatch.data.local.dao

import androidx.room.*
import com.example.breathwatch.data.local.entity.HealthLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneOffset

@Dao
interface HealthLogDao {
    
    @Query("SELECT * FROM health_logs WHERE id = :id")
    suspend fun getHealthLogById(id: Long): HealthLogEntity?
    
    @Query("SELECT * FROM health_logs WHERE date = :date")
    fun observeHealthLogByDate(date: Long): Flow<HealthLogEntity?>
    
    @Query("""
        SELECT * FROM health_logs 
        WHERE date >= :startDate AND date <= :endDate
        ORDER BY date DESC
    """)
    fun getHealthLogsInDateRange(startDate: Long, endDate: Long): Flow<List<HealthLogEntity>>
    
    @Query("""
        SELECT * FROM health_logs 
        WHERE date >= :startDate AND date <= :endDate
        AND (hasCough = 1 OR hasSoreThroat = 1 OR hasWheeze = 1 OR hasShortnessOfBreath = 1 OR hasRunnyNose = 1)
        ORDER BY date DESC
    """)
    fun getSymptomaticLogsInDateRange(startDate: Long, endDate: Long): Flow<List<HealthLogEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthLog(healthLog: HealthLogEntity): Long
    
    @Update
    suspend fun updateHealthLog(healthLog: HealthLogEntity)
    
    @Delete
    suspend fun deleteHealthLog(healthLog: HealthLogEntity)
    
    @Query("DELETE FROM health_logs WHERE id = :id")
    suspend fun deleteHealthLogById(id: Long)
    
    @Query("SELECT * FROM health_logs ORDER BY date DESC LIMIT 1")
    fun observeLatestHealthLog(): Flow<HealthLogEntity?>
    
    @Query("""
        SELECT * FROM health_logs 
        WHERE date >= :startOfDay AND date < :endOfDay
    """)
    fun getHealthLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<HealthLogEntity>>
    
    @Query("""
        SELECT * FROM health_logs 
        WHERE date >= :startOfWeek AND date < :endOfWeek
        ORDER BY date ASC
    """)
    fun getHealthLogsForWeek(startOfWeek: Long, endOfWeek: Long): Flow<List<HealthLogEntity>>
    
    @Query("""
        SELECT * FROM health_logs 
        WHERE pm25 IS NOT NULL AND pm10 IS NOT NULL
        ORDER BY date DESC
    """)
    fun getHealthLogsWithAirQuality(): Flow<List<HealthLogEntity>>
    
    companion object {
        fun getDateMidnightEpochMillis(date: LocalDate): Long {
            return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        }
        
        fun getDateEndOfDayEpochMillis(date: LocalDate): Long {
            return date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() - 1
        }
    }
}
