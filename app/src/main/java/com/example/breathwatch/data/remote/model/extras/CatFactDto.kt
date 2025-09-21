package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class CatFactDto(
    @Json(name = "fact")
    val fact: String,
    @Json(name = "length")
    val length: Int
)
