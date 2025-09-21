package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.CatFactDto
import retrofit2.http.GET

interface CatFactApi {

    @GET("fact")
    suspend fun getCatFact(): CatFactDto
}
