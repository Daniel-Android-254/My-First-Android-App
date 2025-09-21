package com.example.breathwatch.data.remote.response

import com.google.gson.annotations.SerializedName
import com.example.breathwatch.data.local.entity.WeatherEntity
import java.time.Instant
import java.time.ZoneId

data class WeatherResponse(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather,
    @SerializedName("hourly")
    val hourly: HourlyWeather
)

data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("windspeed")
    val windSpeed: Double,
    @SerializedName("winddirection")
    val windDirection: Double,
    @SerializedName("time")
    val time: String
)

data class HourlyWeather(
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature: List<Double>,
    @SerializedName("relative_humidity_2m")
    val humidity: List<Double>,
    @SerializedName("precipitation")
    val precipitation: List<Double>,
    @SerializedName("wind_speed_10m")
    val windSpeed: List<Double>,
    @SerializedName("wind_direction_10m")
    val windDirection: List<Double>
)

fun WeatherResponse.toWeatherEntity(): WeatherEntity {
    val currentTime = Instant.now()
    val zoneId = ZoneId.of(timezone)
    val localDateTime = currentTime.atZone(zoneId)

    // Find the closest hourly data point
    val currentHourIndex = hourly.time.indexOfFirst {
        Instant.parse(it).atZone(zoneId).hour == localDateTime.hour
    }

    return WeatherEntity(
        locationId = "$latitude,$longitude",
        latitude = latitude,
        longitude = longitude,
        temperature = currentWeather.temperature,
        humidity = hourly.humidity.getOrNull(currentHourIndex) ?: 0.0,
        windSpeed = currentWeather.windSpeed,
        windDirection = currentWeather.windDirection,
        precipitation = hourly.precipitation.getOrNull(currentHourIndex) ?: 0.0,
        timestamp = System.currentTimeMillis()
    )
}
