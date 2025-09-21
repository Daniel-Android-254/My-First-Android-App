package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class BitcoinPriceDto(
    @Json(name = "time")
    val time: Time,
    @Json(name = "disclaimer")
    val disclaimer: String,
    @Json(name = "chartName")
    val chartName: String,
    @Json(name = "bpi")
    val bpi: Bpi
)

data class Time(
    @Json(name = "updated")
    val updated: String,
    @Json(name = "updatedISO")
    val updatedISO: String,
    @Json(name = "updateduk")
    val updateduk: String
)

data class Bpi(
    @Json(name = "USD")
    val uSD: USD,
    @Json(name = "GBP")
    val gBP: GBP,
    @Json(name = "EUR")
    val eUR: EUR
)

data class USD(
    @Json(name = "code")
    val code: String,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "rate")
    val rate: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "rate_float")
    val rateFloat: Double
)

data class GBP(
    @Json(name = "code")
    val code: String,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "rate")
    val rate: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "rate_float")
    val rateFloat: Double
)

data class EUR(
    @Json(name = "code")
    val code: String,
    @Json(name = "symbol")
    val symbol: String,
    @Json(name = "rate")
    val rate: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "rate_float")
    val rateFloat: Double
)
