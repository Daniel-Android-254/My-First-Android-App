package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.BitcoinPriceDto
import retrofit2.http.GET

interface BitcoinPriceApi {

    @GET("currentprice.json")
    suspend fun getBitcoinPrice(): BitcoinPriceDto
}
