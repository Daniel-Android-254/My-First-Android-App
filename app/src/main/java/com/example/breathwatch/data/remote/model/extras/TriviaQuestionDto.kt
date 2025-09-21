package com.example.breathwatch.data.remote.model.extras

import com.squareup.moshi.Json

data class TriviaQuestionDto(
    @Json(name = "category")
    val category: String,
    @Json(name = "id")
    val id: String,
    @Json(name = "question")
    val question: String,
    @Json(name = "tags")
    val tags: List<String>,
    @Json(name = "type")
    val type: String,
    @Json(name = "difficulty")
    val difficulty: String,
    @Json(name = "regions")
    val regions: List<String>,
    @Json(name = "isNiche")
    val isNiche: Boolean
)
