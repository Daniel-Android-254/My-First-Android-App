package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.CatFactResponse
import retrofit2.http.GET

interface CatFactsApi {
    
    @GET("fact")
    suspend fun getRandomCatFact(): CatFactResponse
}
