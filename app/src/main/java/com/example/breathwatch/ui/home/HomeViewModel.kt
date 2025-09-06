package com.example.breathwatch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.AirQualityData
import com.example.breathwatch.domain.model.WeatherData
import com.example.breathwatch.domain.usecase.GetAirQualityUseCase
import com.example.breathwatch.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAirQualityUseCase: GetAirQualityUseCase,
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            combine(
                getAirQualityUseCase.observeCached(),
                getWeatherUseCase.observeCached()
            ) { airQuality, weather ->
                _uiState.value = _uiState.value.copy(
                    airQuality = airQuality,
                    weather = weather,
                    isLoading = false
                )
            }
        }
    }

    fun refreshData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Fetch air quality data
                val airQualityResult = getAirQualityUseCase.execute(latitude, longitude)
                if (airQualityResult.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to fetch air quality data: ${airQualityResult.exceptionOrNull()?.message}",
                        isLoading = false
                    )
                    return@launch
                }

                // Fetch weather data
                val weatherResult = getWeatherUseCase.execute(latitude, longitude)
                if (weatherResult.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to fetch weather data: ${weatherResult.exceptionOrNull()?.message}",
                        isLoading = false
                    )
                    return@launch
                }

                _uiState.value = _uiState.value.copy(
                    airQuality = airQualityResult.getOrNull(),
                    weather = weatherResult.getOrNull(),
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "An unexpected error occurred: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class HomeUiState(
    val airQuality: AirQualityData? = null,
    val weather: WeatherData? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
