package com.example.breathwatch.domain.usecase

import com.example.breathwatch.data.local.entity.AirQualityEntity
import com.example.breathwatch.domain.model.AqiCategory
import com.example.breathwatch.domain.repository.AirQualityRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat

class GetAirQualityUseCaseTest {

    @Mock
    private lateinit var airQualityRepository: AirQualityRepository

    private lateinit var getAirQualityUseCase: GetAirQualityUseCase

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        getAirQualityUseCase = GetAirQualityUseCase(airQualityRepository)
    }

    @Test
    fun `execute returns success when repository returns data`() = runTest {
        // Given
        val latitude = -1.2921
        val longitude = 36.8219
        val airQualityEntity = AirQualityEntity(
            id = "test_id",
            latitude = latitude,
            longitude = longitude,
            locationName = "Nairobi, Kenya",
            pm25 = 25.0,
            pm10 = 40.0,
            o3 = null,
            no2 = null,
            so2 = null,
            co = null,
            aqi = 75,
            aqiCategory = 1, // Moderate
            lastUpdated = System.currentTimeMillis(),
            isCached = false
        )

        whenever(airQualityRepository.getAirQuality(latitude, longitude))
            .thenReturn(Result.success(airQualityEntity))

        // When
        val result = getAirQualityUseCase.execute(latitude, longitude)

        // Then
        assertThat(result.isSuccess).isTrue()
        val airQualityData = result.getOrNull()
        assertThat(airQualityData).isNotNull()
        assertThat(airQualityData?.latitude).isEqualTo(latitude)
        assertThat(airQualityData?.longitude).isEqualTo(longitude)
        assertThat(airQualityData?.pm25).isEqualTo(25.0)
        assertThat(airQualityData?.aqiCategory).isEqualTo(AqiCategory.MODERATE)
    }

    @Test
    fun `execute returns failure when repository fails`() = runTest {
        // Given
        val latitude = -1.2921
        val longitude = 36.8219
        val exception = Exception("Network error")

        whenever(airQualityRepository.getAirQuality(latitude, longitude))
            .thenReturn(Result.failure(exception))

        // When
        val result = getAirQualityUseCase.execute(latitude, longitude)

        // Then
        assertThat(result.isFailure).isTrue()
        assertThat(result.exceptionOrNull()).isEqualTo(exception)
    }

    @Test
    fun `observe returns mapped domain model`() = runTest {
        // Given
        val latitude = -1.2921
        val longitude = 36.8219
        val airQualityEntity = AirQualityEntity(
            id = "test_id",
            latitude = latitude,
            longitude = longitude,
            locationName = "Nairobi, Kenya",
            pm25 = 15.0,
            pm10 = 25.0,
            o3 = null,
            no2 = null,
            so2 = null,
            co = null,
            aqi = 45,
            aqiCategory = 0, // Good
            lastUpdated = System.currentTimeMillis(),
            isCached = false
        )

        whenever(airQualityRepository.observeAirQuality(latitude, longitude))
            .thenReturn(flowOf(airQualityEntity))

        // When
        val flow = getAirQualityUseCase.observe(latitude, longitude)

        // Then
        flow.collect { airQualityData ->
            assertThat(airQualityData).isNotNull()
            assertThat(airQualityData?.aqiCategory).isEqualTo(AqiCategory.GOOD)
            assertThat(airQualityData?.pm25).isEqualTo(15.0)
        }
    }
}
