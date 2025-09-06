package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.AirQualityResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApi {
    
    @GET("latest")
    suspend fun getAirQuality(
        @Query("coordinates") coordinates: String,
        @Query("radius") radius: Int = 5000, // 5km radius
        @Query("limit") limit: Int = 1,
        @Query("order_by") orderBy: String = "distance"
    ): AirQualityResponse
    
    @GET("latest")
    suspend fun getAirQualityByCity(
        @Query("city") city: String,
        @Query("limit") limit: Int = 1
    ): AirQualityResponse
}
