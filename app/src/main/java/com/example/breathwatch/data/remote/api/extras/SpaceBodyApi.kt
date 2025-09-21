package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.SpaceBodyDto
import retrofit2.http.GET

interface SpaceBodyApi {

    @GET("bodies")
    suspend fun getSpaceBodies(): SpaceBodyDto
}
