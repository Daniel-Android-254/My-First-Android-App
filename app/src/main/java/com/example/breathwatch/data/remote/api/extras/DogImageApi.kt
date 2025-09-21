package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.DogImageDto
import retrofit2.http.GET

interface DogImageApi {

    @GET("breeds/image/random")
    suspend fun getDogImage(): DogImageDto
}
