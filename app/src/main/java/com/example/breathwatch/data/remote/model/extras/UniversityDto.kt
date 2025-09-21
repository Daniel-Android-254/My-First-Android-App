package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class UniversityDto(
    @Json(name = "name")
    val name: String,
    @Json(name = "country")
    val country: String,
    @Json(name = "domain")
    val domain: String,
    @Json(name = "website")
    val website: String
)
