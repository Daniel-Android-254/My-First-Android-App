package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    companion object {
        const val BASE_URL = "https://api.open-meteo.com/v1/"
        const val DEFAULT_TIMEOUT = 30L
        const val DEFAULT_FORECAST_DAYS = 3
    }

    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("timezone") timezone: String = "auto",
        @Query("hourly") hourly: List<String> = listOf(
            "temperature_2m",
            "relative_humidity_2m",
            "precipitation",
            "wind_speed_10m",
            "wind_direction_10m"
        )
    ): WeatherResponse

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: List<String> = listOf(
            "temperature_2m_max",
            "temperature_2m_min",
            "precipitation_sum",
            "wind_speed_10m_max"
        ),
        @Query("timezone") timezone: String = "auto",
        @Query("forecast_days") forecastDays: Int = DEFAULT_FORECAST_DAYS
    ): WeatherForecastResponse

    @GET("geocoding")
    suspend fun searchLocations(
        @Query("name") query: String,
        @Query("count") count: Int = 10,
        @Query("language") language: String = "en"
    ): LocationSearchResponse
}
