package com.example.breathwatch.data.remote

import kotlinx.coroutines.flow.*
import java.io.IOException

inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType?) -> Boolean = { true },
    crossinline onFetchError: suspend (Throwable) -> Unit = { }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        emit(Resource.Loading(data))

        try {
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)
            query().map { Resource.Success(it) }
        } catch (e: IOException) {
            onFetchError(e)
            query().map { Resource.Error(e, it) }
        }
    } else {
        query().map { Resource.Success(it) }
    }

    emitAll(flow)
}

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Loading<T>(val data: T? = null) : Resource<T>()
    data class Error<T>(val throwable: Throwable, val data: T? = null) : Resource<T>()
}
