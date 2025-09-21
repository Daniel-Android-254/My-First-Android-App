package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.TriviaQuestionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApi {

    @GET("questions")
    suspend fun getTriviaQuestion(
        @Query("limit") limit: Int = 1
    ): List<TriviaQuestionDto>
}
