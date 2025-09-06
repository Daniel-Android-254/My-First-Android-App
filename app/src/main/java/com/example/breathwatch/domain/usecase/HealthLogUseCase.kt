package com.example.breathwatch.domain.usecase

import com.example.breathwatch.data.local.entity.HealthLogEntity
import com.example.breathwatch.domain.model.EnvironmentalData
import com.example.breathwatch.domain.model.HealthLog
import com.example.breathwatch.domain.model.Symptom
import com.example.breathwatch.domain.repository.HealthLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class HealthLogUseCase @Inject constructor(
    private val healthLogRepository: HealthLogRepository
) {
    
    suspend fun logDailyCheckIn(
        overallFeeling: Int,
        symptoms: List<Symptom>,
        notes: String?,
        environmentalData: EnvironmentalData?
    ): Result<Long> {
        return healthLogRepository.logDailyCheckIn(
            overallFeeling = overallFeeling,
            hasCough = symptoms.contains(Symptom.COUGH),
            hasSoreThroat = symptoms.contains(Symptom.SORE_THROAT),
            hasWheeze = symptoms.contains(Symptom.WHEEZE),
            hasShortnessOfBreath = symptoms.contains(Symptom.SHORTNESS_OF_BREATH),
            hasRunnyNose = symptoms.contains(Symptom.RUNNY_NOSE),
            notes = notes,
            latitude = environmentalData?.latitude,
            longitude = environmentalData?.longitude,
            pm25 = environmentalData?.pm25,
            pm10 = environmentalData?.pm10,
            temperature = environmentalData?.temperature,
            humidity = environmentalData?.humidity,
            aqi = environmentalData?.aqi
        )
    }
    
    fun getHealthLogsForWeek(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthLog>> {
        return healthLogRepository.getHealthLogsForWeek(startDate, endDate)
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    fun getLatestHealthLog(): Flow<HealthLog?> {
        return healthLogRepository.getLatestHealthLog()
            .map { it?.toDomainModel() }
    }
    
    fun getHealthLogsWithAirQuality(): Flow<List<HealthLog>> {
        return healthLogRepository.getHealthLogsWithAirQuality()
            .map { entities -> entities.map { it.toDomainModel() } }
    }
    
    private fun HealthLogEntity.toDomainModel(): HealthLog {
        val symptoms = mutableListOf<Symptom>()
        if (hasCough) symptoms.add(Symptom.COUGH)
        if (hasSoreThroat) symptoms.add(Symptom.SORE_THROAT)
        if (hasWheeze) symptoms.add(Symptom.WHEEZE)
        if (hasShortnessOfBreath) symptoms.add(Symptom.SHORTNESS_OF_BREATH)
        if (hasRunnyNose) symptoms.add(Symptom.RUNNY_NOSE)
        
        val environmentalData = if (latitude != null || longitude != null || pm25 != null) {
            EnvironmentalData(
                latitude = latitude,
                longitude = longitude,
                pm25 = pm25,
                pm10 = pm10,
                temperature = temperature,
                humidity = humidity,
                aqi = aqi
            )
        } else null
        
        return HealthLog(
            id = id,
            date = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(date),
                ZoneId.systemDefault()
            ),
            overallFeeling = overallFeeling,
            symptoms = symptoms,
            notes = notes,
            environmentalData = environmentalData
        )
    }
}
