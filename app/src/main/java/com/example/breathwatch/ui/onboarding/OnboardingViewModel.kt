package com.example.breathwatch.ui.onboarding

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(showLocationPermission = true)
    }

    fun onLocationPermissionGranted() {
        _uiState.value = _uiState.value.copy(
            showLocationPermission = false,
            hasLocationPermission = true,
            canProceed = true
        )
    }

    fun onLocationPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            showLocationPermission = false,
            showManualLocationEntry = true,
            hasLocationPermission = false
        )
    }

    fun onSkipLocationPermission() {
        _uiState.value = _uiState.value.copy(
            showLocationPermission = false,
            showManualLocationEntry = true,
            hasLocationPermission = false
        )
    }

    fun onManualLocationChanged(location: String) {
        _uiState.value = _uiState.value.copy(
            manualLocation = location,
            canProceed = location.isNotBlank(),
            locationError = null
        )
    }

    fun saveManualLocation(location: String) {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[stringPreferencesKey("user_location_name")] = location
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    locationError = "Failed to save location: ${e.message}"
                )
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                dataStore.edit { preferences ->
                    preferences[booleanPreferencesKey("is_onboarding_complete")] = true
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
}

data class OnboardingUiState(
    val showLocationPermission: Boolean = false,
    val showManualLocationEntry: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val manualLocation: String = "",
    val canProceed: Boolean = false,
    val locationError: String? = null
)
