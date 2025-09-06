package com.example.breathwatch.ui.screens.airquality

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.AirQualityData
import com.example.breathwatch.domain.usecase.GetAirQualityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AirQualityViewModel @Inject constructor(
    private val getAirQualityUseCase: GetAirQualityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AirQualityUiState())
    val uiState: StateFlow<AirQualityUiState> = _uiState.asStateFlow()

    init {
        observeCachedAirQuality()
    }

    private fun observeCachedAirQuality() {
        viewModelScope.launch {
            getAirQualityUseCase.observeCached().collect { airQuality ->
                _uiState.value = _uiState.value.copy(
                    airQuality = airQuality,
                    isLoading = false
                )
            }
        }
    }

    fun refreshAirQuality(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val result = getAirQualityUseCase.execute(latitude, longitude)
                if (result.isFailure) {
                    _uiState.value = _uiState.value.copy(
                        error = "Failed to fetch air quality data: ${result.exceptionOrNull()?.message}",
                        isLoading = false
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        airQuality = result.getOrNull(),
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

data class AirQualityUiState(
    val airQuality: AirQualityData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
