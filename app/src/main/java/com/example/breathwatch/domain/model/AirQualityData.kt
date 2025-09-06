package com.example.breathwatch.domain.model

import java.time.LocalDateTime

data class AirQualityData(
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val pm25: Double?,
    val pm10: Double?,
    val o3: Double?,
    val no2: Double?,
    val so2: Double?,
    val co: Double?,
    val aqi: Int?,
    val aqiCategory: AqiCategory,
    val lastUpdated: LocalDateTime,
    val isStale: Boolean = false
) {
    val aqiCategoryText: String
        get() = when (aqiCategory) {
            AqiCategory.GOOD -> "Good"
            AqiCategory.MODERATE -> "Moderate"
            AqiCategory.UNHEALTHY_FOR_SENSITIVE -> "Unhealthy for Sensitive Groups"
            AqiCategory.UNHEALTHY -> "Unhealthy"
            AqiCategory.VERY_UNHEALTHY -> "Very Unhealthy"
            AqiCategory.HAZARDOUS -> "Hazardous"
            AqiCategory.UNKNOWN -> "Unknown"
        }

    val safetyAdvice: String
        get() = when (aqiCategory) {
            AqiCategory.GOOD -> "Air quality is good. Great day for outdoor activities!"
            AqiCategory.MODERATE -> "Air quality is acceptable. Sensitive individuals should consider limiting prolonged outdoor activities."
            AqiCategory.UNHEALTHY_FOR_SENSITIVE -> "Sensitive groups should reduce outdoor activities. Consider wearing a mask if you must go outside."
            AqiCategory.UNHEALTHY -> "Everyone should limit outdoor activities. Wear a mask when outside and keep windows closed."
            AqiCategory.VERY_UNHEALTHY -> "Avoid outdoor activities. Stay indoors with air purification if possible."
            AqiCategory.HAZARDOUS -> "Emergency conditions. Avoid all outdoor activities. Seek medical attention if experiencing symptoms."
            AqiCategory.UNKNOWN -> "Air quality data unavailable. Use caution when outdoors."
        }
}

enum class AqiCategory {
    GOOD,
    MODERATE,
    UNHEALTHY_FOR_SENSITIVE,
    UNHEALTHY,
    VERY_UNHEALTHY,
    HAZARDOUS,
    UNKNOWN
}
