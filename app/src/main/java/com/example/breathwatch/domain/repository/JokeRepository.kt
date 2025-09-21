package com.example.breathwatch.domain.repository

import com.example.breathwatch.domain.model.JokeData
import kotlinx.coroutines.flow.Flow

interface JokeRepository {
    suspend fun getJoke(): Result<JokeData>
    fun observeCachedJoke(): Flow<JokeData?>
}
