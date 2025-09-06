package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.QuoteResponse
import retrofit2.http.GET

interface QuotesApi {
    
    @GET("api/random")
    suspend fun getRandomQuote(): List<QuoteResponse>
}
