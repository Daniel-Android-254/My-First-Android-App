package com.example.breathwatch.ui.log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.EnvironmentalData
import com.example.breathwatch.domain.model.HealthLog
import com.example.breathwatch.domain.model.Symptom
import com.example.breathwatch.domain.usecase.GetAirQualityUseCase
import com.example.breathwatch.domain.usecase.GetWeatherUseCase
import com.example.breathwatch.domain.usecase.HealthLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HealthLogViewModel @Inject constructor(
    private val healthLogUseCase: HealthLogUseCase,
    private val getAirQualityUseCase: GetAirQualityUseCase,
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthLogUiState())
    val uiState: StateFlow<HealthLogUiState> = _uiState.asStateFlow()

    init {
        loadWeeklyLogs()
        loadTodaysLog()
    }

    private fun loadWeeklyLogs() {
        viewModelScope.launch {
            val endDate = LocalDate.now()
            val startDate = endDate.minusDays(6)
            
            healthLogUseCase.getHealthLogsForWeek(startDate, endDate).collect { logs ->
                _uiState.value = _uiState.value.copy(weeklyLogs = logs)
            }
        }
    }

    private fun loadTodaysLog() {
        viewModelScope.launch {
            healthLogUseCase.getLatestHealthLog().collect { log ->
                val today = LocalDate.now()
                val isToday = log?.date?.toLocalDate() == today
                
                if (isToday && log != null) {
                    _uiState.value = _uiState.value.copy(
                        todaysLog = log,
                        overallFeeling = log.overallFeeling,
                        selectedSymptoms = log.symptoms.toSet(),
                        notes = log.notes ?: "",
                        hasLoggedToday = true
                    )
                }
            }
        }
    }

    fun updateOverallFeeling(feeling: Int) {
        _uiState.value = _uiState.value.copy(overallFeeling = feeling)
    }

    fun toggleSymptom(symptom: Symptom) {
        val currentSymptoms = _uiState.value.selectedSymptoms.toMutableSet()
        if (currentSymptoms.contains(symptom)) {
            currentSymptoms.remove(symptom)
        } else {
            currentSymptoms.add(symptom)
        }
        _uiState.value = _uiState.value.copy(selectedSymptoms = currentSymptoms)
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun saveHealthLog(latitude: Double? = null, longitude: Double? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Get current environmental data if location is available
                val environmentalData = if (latitude != null && longitude != null) {
                    val airQualityResult = getAirQualityUseCase.execute(latitude, longitude)
                    val weatherResult = getWeatherUseCase.execute(latitude, longitude)
                    
                    EnvironmentalData(
                        latitude = latitude,
                        longitude = longitude,
                        pm25 = airQualityResult.getOrNull()?.pm25,
                        pm10 = airQualityResult.getOrNull()?.pm10,
                        temperature = weatherResult.getOrNull()?.temperatureCelsius,
                        humidity = weatherResult.getOrNull()?.humidity?.toDouble(),
                        aqi = airQualityResult.getOrNull()?.aqi
                    )
                } else null

                val result = healthLogUseCase.logDailyCheckIn(
                    overallFeeling = _uiState.value.overallFeeling,
                    symptoms = _uiState.value.selectedSymptoms.toList(),
                    notes = _uiState.value.notes.takeIf { it.isNotBlank() },
                    environmentalData = environmentalData
                )

                if (result.isSuccess) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasLoggedToday = true,
                        showSuccessMessage = true
                    )
                    loadWeeklyLogs() // Refresh the weekly logs
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed to save health log: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "An unexpected error occurred: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(showSuccessMessage = false)
    }

    fun resetForm() {
        _uiState.value = _uiState.value.copy(
            overallFeeling = 3,
            selectedSymptoms = emptySet(),
            notes = "",
            hasLoggedToday = false,
            todaysLog = null
        )
    }
}

data class HealthLogUiState(
    val overallFeeling: Int = 3,
    val selectedSymptoms: Set<Symptom> = emptySet(),
    val notes: String = "",
    val weeklyLogs: List<HealthLog> = emptyList(),
    val todaysLog: HealthLog? = null,
    val hasLoggedToday: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showSuccessMessage: Boolean = false
)
