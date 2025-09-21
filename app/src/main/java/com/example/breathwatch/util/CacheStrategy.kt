package com.example.breathwatch.util

import androidx.room.withTransaction
import com.example.breathwatch.data.local.AppDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import kotlin.time.Duration.Companion.hours

class CacheStrategy @Inject constructor(
    private val db: AppDatabase,
    private val connectivityObserver: ConnectivityObserver
) {
    suspend fun <T> cachingStrategy(
        dbQuery: suspend () -> Flow<T>,
        networkCall: suspend () -> Result<T>,
        saveCallResult: suspend (T) -> Unit,
        shouldFetch: suspend (T?) -> Boolean = { true },
        maxAge: Long = 1.hours.inWholeMilliseconds
    ): Flow<Result<T>> = flow {
        // First, load from database
        val data = dbQuery().first()

        val shouldMakeNetworkCall = shouldFetch(data)
        if (shouldMakeNetworkCall) {
            // Emit cached data first
            emit(Result.success(data))

            try {
                // Check network connectivity
                if (!connectivityObserver.isNetworkAvailable()) {
                    emit(Result.Error(Exception("No internet connection")))
                    return@flow
                }

                // Make network call
                val networkResult = networkCall()
                networkResult.onSuccess { newData ->
                    // Save the result to the database
                    db.withTransaction {
                        saveCallResult(newData)
                    }
                    // Emit the new data
                    emit(Result.success(newData))
                }.onFailure { error ->
                    // If we have cached data, emit error with data
                    if (data != null) {
                        emit(Result.Error(error, data))
                    } else {
                        emit(Result.Error(error))
                    }
                }
            } catch (e: Exception) {
                // If we have cached data, emit error with data
                if (data != null) {
                    emit(Result.Error(e, data))
                } else {
                    emit(Result.Error(e))
                }
            }
        } else {
            // If we don't need to fetch, just emit the cached data
            emit(Result.success(data))
        }
    }

    suspend fun refreshData(
        refreshWork: suspend () -> Unit,
        maxAttempts: Int = 3,
        initialDelayMillis: Long = 1000
    ) {
        var currentDelay = initialDelayMillis
        var attempts = 0

        while (attempts < maxAttempts) {
            try {
                if (connectivityObserver.isNetworkAvailable()) {
                    refreshWork()
                    break
                }
            } catch (e: Exception) {
                attempts++
                if (attempts >= maxAttempts) throw e
                delay(currentDelay)
                currentDelay *= 2 // Exponential backoff
            }
        }
    }
}
