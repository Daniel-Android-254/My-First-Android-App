package com.example.breathwatch.data.repository

import com.example.breathwatch.data.local.dao.HealthLogDao
import com.example.breathwatch.data.local.entity.HealthLogEntity
import com.example.breathwatch.domain.repository.HealthLogRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthLogRepositoryImpl @Inject constructor(
    private val healthLogDao: HealthLogDao
) : HealthLogRepository {

    override fun getHealthLogsForDay(date: LocalDate): Flow<List<HealthLogEntity>> {
        val startOfDay = HealthLogDao.getDateMidnightEpochMillis(date)
        val endOfDay = HealthLogDao.getDateEndOfDayEpochMillis(date)
        return healthLogDao.getHealthLogsForDay(startOfDay, endOfDay)
    }

    override fun getHealthLogsForWeek(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthLogEntity>> {
        val startOfWeek = HealthLogDao.getDateMidnightEpochMillis(startDate)
        val endOfWeek = HealthLogDao.getDateEndOfDayEpochMillis(endDate)
        return healthLogDao.getHealthLogsForWeek(startOfWeek, endOfWeek)
    }

    override fun getHealthLogById(id: Long): Flow<HealthLogEntity?> {
        return healthLogDao.observeHealthLogByDate(id)
    }

    override fun getLatestHealthLog(): Flow<HealthLogEntity?> {
        return healthLogDao.observeLatestHealthLog()
    }

    override suspend fun saveHealthLog(healthLog: HealthLogEntity): Long {
        return healthLogDao.insertHealthLog(healthLog)
    }

    override suspend fun updateHealthLog(healthLog: HealthLogEntity) {
        healthLogDao.updateHealthLog(healthLog)
    }

    override suspend fun deleteHealthLog(healthLog: HealthLogEntity) {
        healthLogDao.deleteHealthLog(healthLog)
    }

    override fun getSymptomaticLogs(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthLogEntity>> {
        val startTimestamp = HealthLogDao.getDateMidnightEpochMillis(startDate)
        val endTimestamp = HealthLogDao.getDateEndOfDayEpochMillis(endDate)
        return healthLogDao.getSymptomaticLogsInDateRange(startTimestamp, endTimestamp)
    }

    override fun getHealthLogsWithAirQuality(): Flow<List<HealthLogEntity>> {
        return healthLogDao.getHealthLogsWithAirQuality()
    }

    override suspend fun logDailyCheckIn(
        overallFeeling: Int,
        hasCough: Boolean,
        hasSoreThroat: Boolean,
        hasWheeze: Boolean,
        hasShortnessOfBreath: Boolean,
        hasRunnyNose: Boolean,
        notes: String?,
        latitude: Double?,
        longitude: Double?,
        pm25: Double?,
        pm10: Double?,
        temperature: Double?,
        humidity: Double?,
        aqi: Int?
    ): Result<Long> {
        return try {
            val healthLog = HealthLogEntity(
                date = System.currentTimeMillis(),
                overallFeeling = overallFeeling,
                hasCough = hasCough,
                hasSoreThroat = hasSoreThroat,
                hasWheeze = hasWheeze,
                hasShortnessOfBreath = hasShortnessOfBreath,
                hasRunnyNose = hasRunnyNose,
                notes = notes,
                latitude = latitude,
                longitude = longitude,
                pm25 = pm25,
                pm10 = pm10,
                temperature = temperature,
                humidity = humidity?.toDouble(),
                aqi = aqi
            )
            
            val id = healthLogDao.insertHealthLog(healthLog)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
