package com.example.breathwatch.data.remote.response

import com.google.gson.annotations.SerializedName
import com.example.breathwatch.data.local.entity.AirQualityEntity

data class AirQualityResponse(
    @SerializedName("results")
    val results: List<Measurement>,
    @SerializedName("meta")
    val meta: Meta
)

data class Measurement(
    @SerializedName("location")
    val location: String,
    @SerializedName("parameter")
    val parameter: String,
    @SerializedName("value")
    val value: Double,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("coordinates")
    val coordinates: Coordinates,
    @SerializedName("date")
    val date: MeasurementDate,
    @SerializedName("city")
    val city: String? = null,
    @SerializedName("country")
    val country: String? = null
)

data class Coordinates(
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double
)

data class MeasurementDate(
    @SerializedName("utc")
    val utc: String,
    @SerializedName("local")
    val local: String
)

data class Meta(
    @SerializedName("name")
    val name: String,
    @SerializedName("license")
    val license: String,
    @SerializedName("website")
    val website: String,
    @SerializedName("page")
    val page: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("found")
    val found: Int
)

fun List<Measurement>.toAirQualityEntity(): AirQualityEntity {
    val measurements = groupBy { it.parameter.lowercase() }
    return AirQualityEntity(
        locationId = "${coordinates.first().latitude},${coordinates.first().longitude}",
        location = first().location,
        city = first().city,
        country = first().country,
        latitude = coordinates.first().latitude,
        longitude = coordinates.first().longitude,
        pm25 = measurements["pm25"]?.firstOrNull()?.value,
        pm10 = measurements["pm10"]?.firstOrNull()?.value,
        o3 = measurements["o3"]?.firstOrNull()?.value,
        no2 = measurements["no2"]?.firstOrNull()?.value,
        so2 = measurements["so2"]?.firstOrNull()?.value,
        co = measurements["co"]?.firstOrNull()?.value,
        timestamp = System.currentTimeMillis(),
        aqi = calculateAQI(measurements)  // Implement AQI calculation based on measurements
    )
}

private fun calculateAQI(measurements: Map<String, List<Measurement>>): Int {
    // Calculate AQI based on EPA standards
    // Reference: https://www.airnow.gov/aqi/aqi-calculator-concentration/
    val pm25Value = measurements["pm25"]?.firstOrNull()?.value
    val pm10Value = measurements["pm10"]?.firstOrNull()?.value
    val o3Value = measurements["o3"]?.firstOrNull()?.value
    val no2Value = measurements["no2"]?.firstOrNull()?.value

    val aqiValues = mutableListOf<Int>()

    pm25Value?.let { aqiValues.add(calculatePM25AQI(it)) }
    pm10Value?.let { aqiValues.add(calculatePM10AQI(it)) }
    o3Value?.let { aqiValues.add(calculateO3AQI(it)) }
    no2Value?.let { aqiValues.add(calculateNO2AQI(it)) }

    return aqiValues.maxOrNull() ?: 0
}

private fun calculatePM25AQI(concentration: Double): Int {
    // PM2.5 breakpoints (μg/m3) and corresponding AQI values
    return when {
        concentration <= 12.0 -> linearScale(concentration, 0.0, 12.0, 0, 50)
        concentration <= 35.4 -> linearScale(concentration, 12.1, 35.4, 51, 100)
        concentration <= 55.4 -> linearScale(concentration, 35.5, 55.4, 101, 150)
        concentration <= 150.4 -> linearScale(concentration, 55.5, 150.4, 151, 200)
        concentration <= 250.4 -> linearScale(concentration, 150.5, 250.4, 201, 300)
        concentration <= 500.4 -> linearScale(concentration, 250.5, 500.4, 301, 500)
        else -> 500
    }
}

// Similar functions for PM10, O3, and NO2 AQI calculations...

private fun linearScale(conc: Double, cLow: Double, cHigh: Double, iLow: Int, iHigh: Int): Int {
    return ((iHigh - iLow) / (cHigh - cLow) * (conc - cLow) + iLow).toInt()
}
