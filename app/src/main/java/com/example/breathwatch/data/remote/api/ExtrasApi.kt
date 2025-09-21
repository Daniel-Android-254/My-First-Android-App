package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.CatFactResponse
import retrofit2.http.GET

interface ExtrasApi {
    companion object {
        // Using the public Cat Facts API
        const val CAT_FACTS_BASE_URL = "https://catfact.ninja/"
        const val DEFAULT_TIMEOUT = 30L
    }

    @GET("fact")
    suspend fun getCatFact(): CatFactResponse
}
