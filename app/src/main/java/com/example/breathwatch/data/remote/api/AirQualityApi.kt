package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.AirQualityResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AirQualityApi {
    companion object {
        const val BASE_URL = "https://api.openaq.org/v2/"
        const val DEFAULT_TIMEOUT = 30L // 30 seconds
        const val DEFAULT_RADIUS = 5000 // 5km radius
        const val DEFAULT_LIMIT = 1
    }

    @GET("measurements")
    suspend fun getAirQuality(
        @Query("coordinates") coordinates: String,
        @Query("radius") radius: Int = DEFAULT_RADIUS,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("order_by") orderBy: String = "distance",
        @Query("parameter") parameters: List<String> = listOf("pm25", "pm10", "o3", "no2", "so2", "co"),
        @Query("date_from") dateFrom: String? = null,
        @Query("date_to") dateTo: String? = null
    ): AirQualityResponse
    
    @GET("measurements")
    suspend fun getAirQualityByCity(
        @Query("city") city: String,
        @Query("limit") limit: Int = DEFAULT_LIMIT,
        @Query("parameter") parameters: List<String> = listOf("pm25", "pm10", "o3", "no2", "so2", "co")
    ): AirQualityResponse

    @GET("locations")
    suspend fun getNearbyLocations(
        @Query("coordinates") coordinates: String,
        @Query("radius") radius: Int = DEFAULT_RADIUS,
        @Query("limit") limit: Int = 5,
        @Query("order_by") orderBy: String = "distance",
        @Query("has_measurements") hasMeasurements: Boolean = true
    ): LocationResponse

    @GET("countries")
    suspend fun getSupportedCountries(): CountriesResponse
}
