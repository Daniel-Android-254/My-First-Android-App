package com.example.breathwatch.data.remote.response

import com.google.gson.annotations.SerializedName

data class LocationSearchResponse(
    @SerializedName("results")
    val results: List<LocationResult>,
    @SerializedName("generationtime_ms")
    val generationTime: Double
)

data class LocationResult(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("country")
    val country: String,
    @SerializedName("admin1")
    val state: String?,
    @SerializedName("admin2")
    val city: String?,
    @SerializedName("timezone")
    val timezone: String
) {
    fun getDisplayName(): String {
        return buildString {
            append(name)
            if (!city.isNullOrBlank() && city != name) {
                append(", $city")
            }
            if (!state.isNullOrBlank()) {
                append(", $state")
            }
            append(", $country")
        }
    }
}
