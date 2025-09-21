package com.example.breathwatch.data.remote.response

import com.google.gson.annotations.SerializedName
import com.example.breathwatch.data.local.entity.WeatherEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class WeatherForecastResponse(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("timezone")
    val timezone: String,
    @SerializedName("daily")
    val daily: DailyForecast
)

data class DailyForecast(
    @SerializedName("time")
    val time: List<String>,
    @SerializedName("temperature_2m_max")
    val maxTemperature: List<Double>,
    @SerializedName("temperature_2m_min")
    val minTemperature: List<Double>,
    @SerializedName("precipitation_sum")
    val precipitation: List<Double>,
    @SerializedName("wind_speed_10m_max")
    val maxWindSpeed: List<Double>
)

fun WeatherForecastResponse.toWeatherEntities(): List<WeatherEntity> {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    return daily.time.mapIndexed { index, date ->
        WeatherEntity(
            locationId = "$latitude,$longitude",
            latitude = latitude,
            longitude = longitude,
            temperature = (daily.maxTemperature[index] + daily.minTemperature[index]) / 2,
            maxTemperature = daily.maxTemperature[index],
            minTemperature = daily.minTemperature[index],
            windSpeed = daily.maxWindSpeed[index],
            precipitation = daily.precipitation[index],
            forecastDate = LocalDate.parse(date, formatter),
            timestamp = System.currentTimeMillis(),
            isForecast = true
        )
    }
}
