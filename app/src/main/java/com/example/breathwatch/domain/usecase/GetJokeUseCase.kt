package com.example.breathwatch.domain.usecase

import com.example.breathwatch.domain.model.JokeData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetJokeUseCase @Inject constructor(
    // TODO: Inject repository when implemented
) {
    
    suspend fun execute(): Result<JokeData> {
        return try {
            // TODO: Replace with actual repository call
            // For now, return mock data to make the app compilable
            val mockJoke = JokeData(
                id = 1,
                text = "Why don't scientists trust atoms? Because they make up everything!",
                category = "Programming",
                type = "single",
                isSafe = true,
                lastUpdated = LocalDateTime.now()
            )
            Result.success(mockJoke)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun observeCached(): Flow<JokeData?> {
        // TODO: Return actual cached data from repository
        return flowOf(null)
    }
}
