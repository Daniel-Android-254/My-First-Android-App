package com.example.breathwatch.data.remote.response

import com.google.gson.annotations.SerializedName

data class CatFactResponse(
    @SerializedName("fact")
    val fact: String,
    @SerializedName("length")
    val length: Int
)

fun CatFactResponse.toCatFactData() = CatFactData(
    fact = fact,
    timestamp = System.currentTimeMillis()
)
