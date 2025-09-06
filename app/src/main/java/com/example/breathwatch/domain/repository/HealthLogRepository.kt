package com.example.breathwatch.domain.repository

import com.example.breathwatch.data.local.entity.HealthLogEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HealthLogRepository {
    fun getHealthLogsForDay(date: LocalDate): Flow<List<HealthLogEntity>>
    fun getHealthLogsForWeek(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthLogEntity>>
    fun getHealthLogById(id: Long): Flow<HealthLogEntity?>
    fun getLatestHealthLog(): Flow<HealthLogEntity?>
    
    suspend fun saveHealthLog(healthLog: HealthLogEntity): Long
    suspend fun updateHealthLog(healthLog: HealthLogEntity)
    suspend fun deleteHealthLog(healthLog: HealthLogEntity)
    
    fun getSymptomaticLogs(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthLogEntity>>
    fun getHealthLogsWithAirQuality(): Flow<List<HealthLogEntity>>
    
    suspend fun logDailyCheckIn(
        overallFeeling: Int,
        hasCough: Boolean = false,
        hasSoreThroat: Boolean = false,
        hasWheeze: Boolean = false,
        hasShortnessOfBreath: Boolean = false,
        hasRunnyNose: Boolean = false,
        notes: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        pm25: Double? = null,
        pm10: Double? = null,
        temperature: Double? = null,
        humidity: Double? = null,
        aqi: Int? = null
    ): Result<Long>
}
