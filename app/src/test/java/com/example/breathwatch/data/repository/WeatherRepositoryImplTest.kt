package com.example.breathwatch.data.repository

import com.example.breathwatch.data.local.dao.WeatherDao
import com.example.breathwatch.data.remote.api.WeatherApi
import com.example.breathwatch.util.CacheStrategy
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WeatherRepositoryImplTest {
    private lateinit var weatherApi: WeatherApi
    private lateinit var weatherDao: WeatherDao
    private lateinit var cacheStrategy: CacheStrategy
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setup() {
        weatherApi = mockk()
        weatherDao = mockk()
        cacheStrategy = mockk()
        repository = WeatherRepositoryImpl(weatherApi, weatherDao, cacheStrategy)
    }

    @Test
    fun `getWeather returns cached data when fresh`() = runBlocking {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val weatherResponse = TestData.createMockWeatherResponse(latitude, longitude)
        val weatherEntity = weatherResponse.toWeatherEntity()

        coEvery {
            weatherDao.getLatestWeatherByLocation(latitude, longitude)
        } returns weatherEntity

        // When
        val result = repository.getWeather(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(weatherEntity, (result as Result.Success).data)
    }

    @Test
    fun `getWeather fetches from API when cache stale`() = runBlocking {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val weatherResponse = TestData.createMockWeatherResponse(latitude, longitude)

        coEvery {
            weatherApi.getWeather(
                latitude = latitude,
                longitude = longitude,
                currentWeather = true
            )
        } returns weatherResponse

        // When
        val result = repository.getWeather(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(weatherResponse.toWeatherEntity(), (result as Result.Success).data)
    }

    @Test
    fun `getWeather validates coordinates`() = runBlocking {
        // Given
        val invalidLatitude = 91.0
        val longitude = -74.0060

        // When
        val result = repository.getWeather(invalidLatitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }
}

object TestData {
    fun createMockWeatherResponse(latitude: Double, longitude: Double) = WeatherResponse(
        latitude = latitude,
        longitude = longitude,
        timezone = "America/New_York",
        currentWeather = CurrentWeather(
            temperature = 20.5,
            windSpeed = 5.0,
            windDirection = 180.0,
            time = "2025-09-21T12:00:00Z"
        ),
        hourly = HourlyWeather(
            time = listOf("2025-09-21T12:00:00Z"),
            temperature = listOf(20.5),
            humidity = listOf(65.0),
            precipitation = listOf(0.0),
            windSpeed = listOf(5.0),
            windDirection = listOf(180.0)
        )
    )
}
