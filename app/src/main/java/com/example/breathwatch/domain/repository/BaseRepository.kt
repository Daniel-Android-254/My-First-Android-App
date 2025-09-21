package com.example.breathwatch.domain.repository

import kotlinx.coroutines.flow.Flow
import com.example.breathwatch.util.Result

interface BaseRepository<T> {
    fun observe(): Flow<Result<T>>
    suspend fun refresh(): Result<T>
    suspend fun get(): Result<T>
    suspend fun clear()

    /**
     * Determines if the cached data is stale and needs refreshing
     * @param timestamp The timestamp of the cached data
     * @param maxAge The maximum age of the data in milliseconds
     */
    fun isDataStale(timestamp: Long, maxAge: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - timestamp) > maxAge
    }
}
