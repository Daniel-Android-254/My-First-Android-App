package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.UniversityDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UniversityApi {

    @GET("search")
    suspend fun getUniversities(
        @Query("country") country: String
    ): List<UniversityDto>
}
