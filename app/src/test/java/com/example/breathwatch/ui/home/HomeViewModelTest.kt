package com.example.breathwatch.ui.home

import com.example.breathwatch.domain.model.AirQualityData
import com.example.breathwatch.domain.model.AqiCategory
import com.example.breathwatch.domain.model.WeatherData
import com.example.breathwatch.domain.usecase.GetAirQualityUseCase
import com.example.breathwatch.domain.usecase.GetWeatherUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever
import com.google.common.truth.Truth.assertThat
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @Mock
    private lateinit var getAirQualityUseCase: GetAirQualityUseCase

    @Mock
    private lateinit var getWeatherUseCase: GetWeatherUseCase

    private lateinit var homeViewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        
        // Setup default mock behaviors
        whenever(getAirQualityUseCase.observeCached()).thenReturn(flowOf(null))
        whenever(getWeatherUseCase.observeCached()).thenReturn(flowOf(null))
        
        homeViewModel = HomeViewModel(getAirQualityUseCase, getWeatherUseCase)
    }

    @Test
    fun `initial state is loading`() {
        // When
        val initialState = homeViewModel.uiState.value

        // Then
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.airQuality).isNull()
        assertThat(initialState.weather).isNull()
        assertThat(initialState.error).isNull()
    }

    @Test
    fun `refreshData updates state with success data`() = runTest {
        // Given
        val latitude = -1.2921
        val longitude = 36.8219
        
        val airQualityData = AirQualityData(
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
            aqiCategory = AqiCategory.MODERATE,
            lastUpdated = LocalDateTime.now(),
            isStale = false
        )

        val weatherData = WeatherData(
            latitude = latitude,
            longitude = longitude,
            locationName = "Nairobi, Kenya",
            temperatureCelsius = 22.0,
            temperatureFahrenheit = 71.6,
            conditionText = "Partly Cloudy",
            conditionIcon = null,
            humidity = 65,
            windSpeedKph = 10.0,
            windDirection = "NE",
            precipitationMm = 0.0,
            pressureMb = 1013.0,
            visibilityKm = 10.0,
            cloudCover = 40,
            uvIndex = 6.0,
            lastUpdated = LocalDateTime.now(),
            isStale = false
        )

        whenever(getAirQualityUseCase.execute(latitude, longitude))
            .thenReturn(Result.success(airQualityData))
        whenever(getWeatherUseCase.execute(latitude, longitude))
            .thenReturn(Result.success(weatherData))

        // When
        homeViewModel.refreshData(latitude, longitude)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).isNull()
        assertThat(state.airQuality).isEqualTo(airQualityData)
        assertThat(state.weather).isEqualTo(weatherData)
    }

    @Test
    fun `refreshData updates state with error when air quality fails`() = runTest {
        // Given
        val latitude = -1.2921
        val longitude = 36.8219
        val exception = Exception("Network error")

        whenever(getAirQualityUseCase.execute(latitude, longitude))
            .thenReturn(Result.failure(exception))

        // When
        homeViewModel.refreshData(latitude, longitude)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = homeViewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.error).contains("Failed to fetch air quality data")
        assertThat(state.airQuality).isNull()
        assertThat(state.weather).isNull()
    }

    @Test
    fun `clearError removes error from state`() = runTest {
        // Given - set an error state first
        val latitude = -1.2921
        val longitude = 36.8219
        val exception = Exception("Network error")

        whenever(getAirQualityUseCase.execute(latitude, longitude))
            .thenReturn(Result.failure(exception))

        homeViewModel.refreshData(latitude, longitude)
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify error is set
        assertThat(homeViewModel.uiState.value.error).isNotNull()

        // When
        homeViewModel.clearError()

        // Then
        assertThat(homeViewModel.uiState.value.error).isNull()
    }
}
