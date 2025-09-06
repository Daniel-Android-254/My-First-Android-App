package com.example.breathwatch.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.usecase.HealthLogUseCase
import com.example.breathwatch.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val healthLogUseCase: HealthLogUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            try {
                val preferences = dataStore.data.first()
                
                _uiState.value = _uiState.value.copy(
                    notificationsEnabled = preferences[booleanPreferencesKey("notifications_enabled")] ?: true,
                    aqiThreshold = preferences[intPreferencesKey("aqi_threshold")] ?: Constants.DEFAULT_AQI_THRESHOLD,
                    userLocationName = preferences[stringPreferencesKey("user_location_name")] ?: "",
                    themeMode = preferences[stringPreferencesKey("theme_mode")] ?: "system",
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load settings: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun updateNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[booleanPreferencesKey("notifications_enabled")] = enabled
                }
                _uiState.value = _uiState.value.copy(notificationsEnabled = enabled)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update notification settings: ${e.message}"
                )
            }
        }
    }

    fun updateAqiThreshold(threshold: Int) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[intPreferencesKey("aqi_threshold")] = threshold
                }
                _uiState.value = _uiState.value.copy(aqiThreshold = threshold)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update AQI threshold: ${e.message}"
                )
            }
        }
    }

    fun updateUserLocation(location: String) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("user_location_name")] = location
                }
                _uiState.value = _uiState.value.copy(userLocationName = location)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update location: ${e.message}"
                )
            }
        }
    }

    fun updateThemeMode(theme: String) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("theme_mode")] = theme
                }
                _uiState.value = _uiState.value.copy(themeMode = theme)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to update theme: ${e.message}"
                )
            }
        }
    }

    fun exportHealthData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isExporting = true, error = null)
            
            try {
                // Get all health logs with air quality data
                healthLogUseCase.getHealthLogsWithAirQuality().first().let { logs ->
                    // In a real implementation, this would generate and save a CSV file
                    // For now, we'll just simulate the export
                    kotlinx.coroutines.delay(2000) // Simulate export time
                    
                    _uiState.value = _uiState.value.copy(
                        isExporting = false,
                        showExportSuccess = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isExporting = false,
                    error = "Failed to export data: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearExportSuccess() {
        _uiState.value = _uiState.value.copy(showExportSuccess = false)
    }
}

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val aqiThreshold: Int = Constants.DEFAULT_AQI_THRESHOLD,
    val userLocationName: String = "",
    val themeMode: String = "system",
    val isLoading: Boolean = true,
    val isExporting: Boolean = false,
    val error: String? = null,
    val showExportSuccess: Boolean = false
)
