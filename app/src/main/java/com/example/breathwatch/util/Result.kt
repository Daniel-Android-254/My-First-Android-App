package com.example.breathwatch.util

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()

    fun <R> map(transform: (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
        is Loading -> Loading
    }

    suspend fun <R> suspendMap(transform: suspend (T) -> R): Result<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> Error(exception)
        is Loading -> Loading
    }

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error, Loading -> null
    }

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is in Loading state")
    }

    companion object {
        fun <T> success(data: T) = Success(data)
        fun error(exception: Throwable) = Error(exception)
        fun loading() = Loading

        suspend fun <T> wrap(block: suspend () -> T): Result<T> = try {
            Success(block())
        } catch (e: Exception) {
            Error(e)
        }
    }
}
