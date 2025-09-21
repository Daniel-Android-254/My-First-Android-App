package com.example.breathwatch.data.remote.response

import com.google.gson.annotations.SerializedName

data class LocationResponse(
    @SerializedName("results")
    val results: List<Location>,
    @SerializedName("meta")
    val meta: Meta
)

data class Location(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("city")
    val city: String?,
    @SerializedName("country")
    val country: String,
    @SerializedName("coordinates")
    val coordinates: Coordinates,
    @SerializedName("parameters")
    val parameters: List<Parameter>,
    @SerializedName("lastUpdated")
    val lastUpdated: String,
    @SerializedName("firstUpdated")
    val firstUpdated: String
)

data class Parameter(
    @SerializedName("id")
    val id: Int,
    @SerializedName("parameter")
    val parameter: String,
    @SerializedName("unit")
    val unit: String,
    @SerializedName("lastValue")
    val lastValue: Double,
    @SerializedName("lastUpdated")
    val lastUpdated: String
)
