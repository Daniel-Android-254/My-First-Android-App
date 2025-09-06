package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WeatherApi {
    
    @GET("{location}")
    suspend fun getWeather(
        @Path("location") location: String,
        @Query("format") format: String = "j1"
    ): WeatherResponse
    
    @GET("{location}")
    suspend fun getWeatherForecast(
        @Path("location") location: String,
        @Query("format") format: String = "j1",
        @Query("num_of_days") numOfDays: Int = 3
    ): WeatherResponse
}
