package com.example.breathwatch.ui.screens.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.WeatherData
import com.example.breathwatch.domain.usecase.GetWeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        observeCachedWeather()
    }

    private fun observeCachedWeather() {
        viewModelScope.launch {
            getWeatherUseCase.observeCached().collect { weather ->
                _uiState.value = _uiState.value.copy(
                    weather = weather,
                    isLoading = false
                )
            }
        }
    }

    fun refreshWeather(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = getWeatherUseCase.execute(latitude, longitude)
                if (result.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to fetch weather data: ${result.exceptionOrNull()?.message}",
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        weather = result.getOrNull(),
                        isLoading = false,
                        error = null
                    )
                }
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

data class WeatherUiState(
    val weather: WeatherData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
