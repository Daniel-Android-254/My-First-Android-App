package com.example.breathwatch.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            val request = chain.request()
            val response = chain.proceed(request)

            when (response.code) {
                in 200..299 -> return response
                401 -> throw ApiException.Unauthorized()
                403 -> throw ApiException.Forbidden()
                404 -> throw ApiException.NotFound()
                429 -> throw ApiException.RateLimited()
                in 500..599 -> throw ApiException.Server(response.code)
                else -> throw ApiException.Unknown("Unknown error occurred: ${response.code}")
            }
        } catch (e: SocketTimeoutException) {
            throw ApiException.Network("Connection timed out")
        } catch (e: UnknownHostException) {
            throw ApiException.Network("No internet connection")
        } catch (e: IOException) {
            throw ApiException.Network(e.message ?: "Network error occurred")
        }
    }
}

sealed class ApiException(message: String) : IOException(message) {
    class Network(message: String) : ApiException(message)
    class Unauthorized : ApiException("Unauthorized access")
    class Forbidden : ApiException("Access forbidden")
    class NotFound : ApiException("Resource not found")
    class RateLimited : ApiException("Too many requests. Please try again later")
    class Server(code: Int) : ApiException("Server error occurred: $code")
    class Unknown(message: String) : ApiException(message)
}
