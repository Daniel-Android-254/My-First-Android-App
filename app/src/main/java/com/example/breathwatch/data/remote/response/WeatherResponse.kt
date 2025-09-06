package com.example.breathwatch.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "current_condition") val currentCondition: List<CurrentCondition>,
    @Json(name = "nearest_area") val nearestArea: List<NearestArea>,
    @Json(name = "request") val request: List<Request>,
    @Json(name = "weather") val weather: List<Weather>
)

@JsonClass(generateAdapter = true)
data class CurrentCondition(
    @Json(name = "temp_C") val tempC: String,
    @Json(name = "temp_F") val tempF: String,
    @Json(name = "weatherCode") val weatherCode: String,
    @Json(name = "weatherIconUrl") val weatherIconUrl: List<WeatherIcon>,
    @Json(name = "weatherDesc") val weatherDesc: List<WeatherDesc>,
    @Json(name = "windspeedMiles") val windspeedMiles: String,
    @Json(name = "windspeedKmph") val windspeedKmph: String,
    @Json(name = "winddirDegree") val winddirDegree: String,
    @Json(name = "winddir16Point") val winddir16Point: String,
    @Json(name = "precipMM") val precipMM: String,
    @Json(name = "precipInches") val precipInches: String,
    @Json(name = "humidity") val humidity: String,
    @Json(name = "visibility") val visibility: String,
    @Json(name = "visibilityMiles") val visibilityMiles: String,
    @Json(name = "pressure") val pressure: String,
    @Json(name = "pressureInches") val pressureInches: String,
    @Json(name = "cloudcover") val cloudcover: String,
    @Json(name = "FeelsLikeC") val feelsLikeC: String,
    @Json(name = "FeelsLikeF") val feelsLikeF: String,
    @Json(name = "uvIndex") val uvIndex: String
)

@JsonClass(generateAdapter = true)
data class WeatherIcon(
    @Json(name = "value") val value: String
)

@JsonClass(generateAdapter = true)
data class WeatherDesc(
    @Json(name = "value") val value: String
)

@JsonClass(generateAdapter = true)
data class NearestArea(
    @Json(name = "areaName") val areaName: List<AreaName>,
    @Json(name = "country") val country: List<Country>,
    @Json(name = "region") val region: List<Region>,
    @Json(name = "latitude") val latitude: String,
    @Json(name = "longitude") val longitude: String,
    @Json(name = "population") val population: String,
    @Json(name = "weatherUrl") val weatherUrl: List<WeatherUrl>
)

@JsonClass(generateAdapter = true)
data class AreaName(
    @Json(name = "value") val value: String
)

@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "value") val value: String
)

@JsonClass(generateAdapter = true)
data class Region(
    @Json(name = "value") val value: String
)

@JsonClass(generateAdapter = true)
data class WeatherUrl(
    @Json(name = "value") val value: String
)

@JsonClass(generateAdapter = true)
data class Request(
    @Json(name = "type") val type: String,
    @Json(name = "query") val query: String
)

@JsonClass(generateAdapter = true)
data class Weather(
    @Json(name = "date") val date: String,
    @Json(name = "maxtempC") val maxtempC: String,
    @Json(name = "maxtempF") val maxtempF: String,
    @Json(name = "mintempC") val mintempC: String,
    @Json(name = "mintempF") val mintempF: String,
    @Json(name = "totalSnow_cm") val totalSnowCm: String,
    @Json(name = "sunHour") val sunHour: String,
    @Json(name = "uvIndex") val uvIndex: String,
    @Json(name = "hourly") val hourly: List<Hourly>
)

@JsonClass(generateAdapter = true)
data class Hourly(
    @Json(name = "time") val time: String,
    @Json(name = "tempC") val tempC: String,
    @Json(name = "tempF") val tempF: String,
    @Json(name = "windspeedMiles") val windspeedMiles: String,
    @Json(name = "windspeedKmph") val windspeedKmph: String,
    @Json(name = "winddirDegree") val winddirDegree: String,
    @Json(name = "winddir16Point") val winddir16Point: String,
    @Json(name = "weatherCode") val weatherCode: String,
    @Json(name = "weatherIconUrl") val weatherIconUrl: List<WeatherIcon>,
    @Json(name = "weatherDesc") val weatherDesc: List<WeatherDesc>,
    @Json(name = "precipMM") val precipMM: String,
    @Json(name = "precipInches") val precipInches: String,
    @Json(name = "humidity") val humidity: String,
    @Json(name = "visibility") val visibility: String,
    @Json(name = "visibilityMiles") val visibilityMiles: String,
    @Json(name = "pressure") val pressure: String,
    @Json(name = "pressureInches") val pressureInches: String,
    @Json(name = "cloudcover") val cloudcover: String,
    @Json(name = "HeatIndexC") val heatIndexC: String,
    @Json(name = "HeatIndexF") val heatIndexF: String,
    @Json(name = "DewPointC") val dewPointC: String,
    @Json(name = "DewPointF") val dewPointF: String,
    @Json(name = "WindChillC") val windChillC: String,
    @Json(name = "WindChillF") val windChillF: String,
    @Json(name = "WindGustMiles") val windGustMiles: String,
    @Json(name = "WindGustKmph") val windGustKmph: String,
    @Json(name = "FeelsLikeC") val feelsLikeC: String,
    @Json(name = "FeelsLikeF") val feelsLikeF: String,
    @Json(name = "uvIndex") val uvIndex: String
)

// Extension function to convert API response to domain entity
fun WeatherResponse.toWeatherEntity(): com.example.breathwatch.data.local.entity.WeatherEntity {
    val current = currentCondition.firstOrNull() ?: throw IllegalStateException("No current weather data available")
    val area = nearestArea.firstOrNull()
    
    val locationName = listOfNotNull(
        area?.areaName?.firstOrNull()?.value,
        area?.country?.firstOrNull()?.value
    ).joinToString(separator = ", ")
    
    return com.example.breathwatch.data.local.entity.WeatherEntity(
        id = "${area?.latitude ?: "0"}_${area?.longitude ?: "0"}",
        latitude = area?.latitude?.toDoubleOrNull() ?: 0.0,
        longitude = area?.longitude?.toDoubleOrNull() ?: 0.0,
        locationName = locationName,
        temperatureCelsius = current.tempC.toDoubleOrNull() ?: 0.0,
        temperatureFahrenheit = current.tempF.toDoubleOrNull() ?: 0.0,
        conditionText = current.weatherDesc.firstOrNull()?.value ?: "Unknown",
        conditionIcon = current.weatherIconUrl.firstOrNull()?.value,
        humidity = current.humidity.toIntOrNull() ?: 0,
        windSpeedKph = current.windspeedKmph.toDoubleOrNull() ?: 0.0,
        windDirection = current.winddir16Point,
        precipitationMm = current.precipMM.toDoubleOrNull() ?: 0.0,
        pressureMb = current.pressure.toDoubleOrNull() ?: 0.0,
        visibilityKm = current.visibility.toDoubleOrNull() ?: 0.0,
        cloudCover = current.cloudcover.toIntOrNull() ?: 0,
        uvIndex = current.uvIndex.toDoubleOrNull() ?: 0.0,
        lastUpdated = System.currentTimeMillis(),
        isCached = false
    )
}
