package com.example.breathwatch.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class AirQualityResponse(
    @Json(name = "results") val results: List<AirQualityResult>,
    @Json(name = "meta") val meta: Meta
)

@JsonClass(generateAdapter = true)
data class AirQualityResult(
    @Json(name = "location") val location: String,
    @Json(name = "city") val city: String?,
    @Json(name = "country") val country: String,
    @Json(name = "coordinates") val coordinates: Coordinates,
    @Json(name = "measurements") val measurements: List<Measurement>,
    @Json(name = "lastUpdated") val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class Coordinates(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double
)

@JsonClass(generateAdapter = true)
data class Measurement(
    @Json(name = "parameter") val parameter: String,
    @Json(name = "value") val value: Double,
    @Json(name = "unit") val unit: String,
    @Json(name = "lastUpdated") val lastUpdated: String
)

@JsonClass(generateAdapter = true)
data class Meta(
    @Json(name = "name") val name: String,
    @Json(name = "license") val license: String,
    @Json(name = "website") val website: String,
    @Json(name = "page") val page: Int,
    @Json(name = "limit") val limit: Int,
    @Json(name = "found") val found: Int
)

// Extension functions to map API response to domain model
fun AirQualityResponse.toAirQualityEntity(): com.example.breathwatch.data.local.entity.AirQualityEntity {
    val result = results.firstOrNull() ?: throw IllegalStateException("No air quality data available")
    
    val pm25 = result.measurements.find { it.parameter.equals("pm25", ignoreCase = true) }?.value
    val pm10 = result.measurements.find { it.parameter.equals("pm10", ignoreCase = true) }?.value
    val o3 = result.measurements.find { it.parameter.equals("o3", ignoreCase = true) }?.value
    val no2 = result.measurements.find { it.parameter.equals("no2", ignoreCase = true) }?.value
    val so2 = result.measurements.find { it.parameter.equals("so2", ignoreCase = true) }?.value
    val co = result.measurements.find { it.parameter.equals("co", ignoreCase = true) }?.value
    
    // Calculate AQI based on PM2.5 (simplified version)
    val aqi = when {
        pm25 == null -> null
        pm25 <= 12 -> (50.0 / 12.0 * pm25).toInt() // Good (0-50)
        pm25 <= 35.4 -> 51 + ((99 - 51) / (35.4 - 12.1) * (pm25 - 12.1)).toInt() // Moderate (51-99)
        pm25 <= 55.4 -> 100 + ((149 - 100) / (55.4 - 35.5) * (pm25 - 35.5)).toInt() // Unhealthy for Sensitive Groups (100-149)
        pm25 <= 150.4 -> 150 + ((199 - 150) / (150.4 - 55.5) * (pm25 - 55.5)).toInt() // Unhealthy (150-199)
        pm25 <= 250.4 -> 200 + ((299 - 200) / (250.4 - 150.5) * (pm25 - 150.5)).toInt() // Very Unhealthy (200-299)
        else -> 300 + ((500 - 300) / (500.4 - 250.5) * (pm25 - 250.5)).toInt() // Hazardous (300-500)
    }
    
    // Determine AQI category
    val aqiCategory = when {
        aqi == null -> -1
        aqi <= 50 -> 0 // Good
        aqi <= 100 -> 1 // Moderate
        aqi <= 150 -> 2 // Unhealthy for Sensitive Groups
        aqi <= 200 -> 3 // Unhealthy
        aqi <= 300 -> 4 // Very Unhealthy
        else -> 5 // Hazardous
    }
    
    val locationName = listOfNotNull(
        result.city,
        result.country
    ).joinToString(separator = ", ")
    
    return com.example.breathwatch.data.local.entity.AirQualityEntity(
        id = "${result.coordinates.latitude}_${result.coordinates.longitude}",
        latitude = result.coordinates.latitude,
        longitude = result.coordinates.longitude,
        locationName = locationName,
        pm25 = pm25,
        pm10 = pm10,
        o3 = o3,
        no2 = no2,
        so2 = so2,
        co = co,
        aqi = aqi,
        aqiCategory = aqiCategory,
        lastUpdated = System.currentTimeMillis(),
        isCached = false
    )
}
