package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class DogImageDto(
    @Json(name = "message")
    val message: String,
    @Json(name = "status")
    val status: String
)
