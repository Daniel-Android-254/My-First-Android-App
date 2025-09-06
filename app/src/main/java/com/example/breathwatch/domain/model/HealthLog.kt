package com.example.breathwatch.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class HealthLog(
    val id: Long = 0,
    val date: LocalDateTime,
    val overallFeeling: Int, // 1-5 scale (1 = very poor, 5 = excellent)
    val symptoms: List<Symptom>,
    val notes: String?,
    val environmentalData: EnvironmentalData?
) {
    val feelingDescription: String
        get() = when (overallFeeling) {
            1 -> "Very Poor"
            2 -> "Poor"
            3 -> "Fair"
            4 -> "Good"
            5 -> "Excellent"
            else -> "Unknown"
        }
    
    val hasSymptoms: Boolean
        get() = symptoms.isNotEmpty()
    
    val symptomsText: String
        get() = symptoms.joinToString(", ") { it.displayName }
}

enum class Symptom(val displayName: String) {
    COUGH("Cough"),
    SORE_THROAT("Sore throat"),
    WHEEZE("Wheezing"),
    SHORTNESS_OF_BREATH("Shortness of breath"),
    RUNNY_NOSE("Runny nose")
}

data class EnvironmentalData(
    val latitude: Double?,
    val longitude: Double?,
    val pm25: Double?,
    val pm10: Double?,
    val temperature: Double?,
    val humidity: Double?,
    val aqi: Int?
)
