package com.example.breathwatch.data.repository

import com.example.breathwatch.data.local.dao.AirQualityDao
import com.example.breathwatch.data.remote.api.AirQualityApi
import com.example.breathwatch.util.CacheStrategy
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AirQualityRepositoryImplTest {
    private lateinit var airQualityApi: AirQualityApi
    private lateinit var airQualityDao: AirQualityDao
    private lateinit var cacheStrategy: CacheStrategy
    private lateinit var repository: AirQualityRepositoryImpl

    @Before
    fun setup() {
        airQualityApi = mockk()
        airQualityDao = mockk()
        cacheStrategy = mockk()
        repository = AirQualityRepositoryImpl(airQualityApi, airQualityDao, cacheStrategy)
    }

    @Test
    fun `getAirQuality validates coordinates`() = runBlocking {
        // Given
        val invalidLatitude = 91.0
        val longitude = -74.0060

        // When
        val result = repository.getAirQuality(invalidLatitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }

    @Test
    fun `getAirQuality calculates AQI correctly`() = runBlocking {
        // Given
        val latitude = 40.7128
        val longitude = -74.0060
        val measurements = listOf(
            Measurement(
                location = "Test Location",
                parameter = "pm25",
                value = 35.0,
                unit = "µg/m³",
                coordinates = Coordinates(latitude, longitude),
                date = MeasurementDate("2025-09-21T12:00:00Z", "2025-09-21T12:00:00"),
                city = "Test City",
                country = "Test Country"
            )
        )
        val response = AirQualityResponse(measurements, Meta("test", "test", "test", 1, 1, 1))

        coEvery {
            airQualityApi.getAirQuality("$latitude,$longitude")
        } returns response

        // When
        val result = repository.getAirQuality(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
        val airQuality = (result as Result.Success).data
        // AQI for PM2.5 of 35.0 µg/m³ should be approximately 99
        assertTrue(airQuality.aqi in 95..105)
    }

    @Test
    fun `getAirQualityByLocationName validates input`() = runBlocking {
        // Given
        val emptyLocationName = ""

        // When
        val result = repository.getAirQualityByLocationName(emptyLocationName)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }
}
