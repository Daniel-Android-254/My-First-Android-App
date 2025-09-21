package com.example.breathwatch.domain.usecase

import com.example.breathwatch.domain.model.JokeData
import com.example.breathwatch.domain.repository.JokeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetJokeUseCase @Inject constructor(
    private val jokeRepository: JokeRepository
) {

    suspend fun execute(): Result<JokeData> {
        return jokeRepository.getJoke()
    }

    fun observeCached(): Flow<JokeData?> {
        return jokeRepository.observeCachedJoke()
    }
}
