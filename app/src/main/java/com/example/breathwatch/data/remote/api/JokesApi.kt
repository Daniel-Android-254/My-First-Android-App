package com.example.breathwatch.data.remote.api

import com.example.breathwatch.data.remote.response.JokeResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface JokesApi {
    
    @GET("joke/Any")
    suspend fun getRandomJoke(
        @Query("type") type: String = "single"
    ): JokeResponse
}
