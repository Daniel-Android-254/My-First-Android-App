package com.example.breathwatch.data.remote.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuoteResponse(
    @Json(name = "q") val text: String,
    @Json(name = "a") val author: String,
    @Json(name = "i") val image: String?,
    @Json(name = "c") val length: String?,
    @Json(name = "h") val html: String?
)
