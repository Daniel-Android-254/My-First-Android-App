package com.example.breathwatch.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CatFactResponse(
    @Json(name = "fact") val fact: String,
    @Json(name = "length") val length: Int
)
