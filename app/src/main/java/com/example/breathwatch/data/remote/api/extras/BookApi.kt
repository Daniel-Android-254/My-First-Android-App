package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.BookDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BookApi {

    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String
    ): BookDto
}
