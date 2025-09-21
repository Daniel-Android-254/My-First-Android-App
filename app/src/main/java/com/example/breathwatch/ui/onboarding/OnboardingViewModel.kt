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

    fun onAllPermissionsGranted() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETED] = true
                preferences[LOCATION_PERMISSION_GRANTED] = true
                preferences[NOTIFICATIONS_ENABLED] = true
            }
        }
        _uiState.value = _uiState.value.copy(
            hasLocationPermission = true,
            hasNotificationPermission = true,
            canProceed = true
        )
    }

    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[LOCATION_PERMISSION_GRANTED] = true
            }
        }
        _uiState.value = _uiState.value.copy(
            showLocationPermission = false,
            hasLocationPermission = true,
            canProceed = true
        )
    }

    fun onPermissionsDenied() {
        _uiState.value = _uiState.value.copy(
            showLocationPermission = false,
            showManualLocationEntry = true,
            showPermissionRationale = true,
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
            locationError = null,
            canProceed = location.isNotBlank()
        )
    }

    fun showNotificationWarning() {
        _uiState.value = _uiState.value.copy(
            showNotificationWarning = true
        )
    }

    fun dismissPermissionRationale() {
        _uiState.value = _uiState.value.copy(
            showPermissionRationale = false
        )
    }

    fun validateAndSaveManualLocation() {
        val location = _uiState.value.manualLocation.trim()
        if (location.isBlank()) {
            _uiState.value = _uiState.value.copy(
                locationError = "Please enter a valid location"
            )
            return
        }

        viewModelScope.launch {
            dataStore.edit { preferences ->
                preferences[MANUAL_LOCATION] = location
                preferences[ONBOARDING_COMPLETED] = true
            }
        }
        _uiState.value = _uiState.value.copy(
            canProceed = true,
            locationError = null
        )
    }

    companion object {
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val LOCATION_PERMISSION_GRANTED = booleanPreferencesKey("location_permission_granted")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val MANUAL_LOCATION = stringPreferencesKey("manual_location")
    }
}

data class OnboardingUiState(
    val showLocationPermission: Boolean = false,
    val showManualLocationEntry: Boolean = false,
    val showPermissionRationale: Boolean = false,
    val showNotificationWarning: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val manualLocation: String = "",
    val locationError: String? = null,
    val canProceed: Boolean = false
)
