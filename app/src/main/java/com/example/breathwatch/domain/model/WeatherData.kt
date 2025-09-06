package com.example.breathwatch.domain.model

import java.time.LocalDateTime

data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val locationName: String?,
    val temperatureCelsius: Double,
    val temperatureFahrenheit: Double,
    val conditionText: String,
    val conditionIcon: String?,
    val humidity: Int,
    val windSpeedKph: Double,
    val windDirection: String,
    val precipitationMm: Double,
    val pressureMb: Double,
    val visibilityKm: Double,
    val cloudCover: Int,
    val uvIndex: Double,
    val lastUpdated: LocalDateTime,
    val isStale: Boolean = false
) {
    val temperatureDisplay: String
        get() = "${temperatureCelsius.toInt()}°C"
    
    val humidityDisplay: String
        get() = "$humidity%"
    
    val windDisplay: String
        get() = "${windSpeedKph.toInt()} km/h $windDirection"
    
    val uvIndexCategory: String
        get() = when {
            uvIndex <= 2 -> "Low"
            uvIndex <= 5 -> "Moderate"
            uvIndex <= 7 -> "High"
            uvIndex <= 10 -> "Very High"
            else -> "Extreme"
        }
}
