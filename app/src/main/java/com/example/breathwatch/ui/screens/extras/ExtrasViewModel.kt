package com.example.breathwatch.ui.screens.extras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.CatFactData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExtrasViewModel @Inject constructor(
    // TODO: Inject use cases when implemented
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtrasUiState())
    val uiState: StateFlow<ExtrasUiState> = _uiState.asStateFlow()

    fun loadInitialContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // TODO: Replace with actual API calls when use cases are implemented
                // For now, using mock data to demonstrate the UI
                val mockCatFact = CatFactData(
                    fact = "Cats have over 20 muscles that control their ears.",
                    length = 47,
                    lastUpdated = LocalDateTime.now()
                )
                
                _uiState.value = _uiState.value.copy(
                    catFact = mockCatFact,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load content: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun refreshAllContent() {
        loadInitialContent()
    }

    fun refreshApiContent(apiId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                when (apiId) {
                    "catfacts" -> {
                        // TODO: Replace with actual API call when use case is implemented
                        val mockCatFact = CatFactData(
                            fact = "A group of cats is called a 'clowder'.",
                            length = 35,
                            lastUpdated = LocalDateTime.now()
                        )
                        _uiState.value = _uiState.value.copy(catFact = mockCatFact)
                    }
                    // TODO: Add other API implementations
                    else -> {
                        // Placeholder for other APIs
                        _uiState.value = _uiState.value.copy(
                            error = "API '$apiId' not yet implemented"
                        )
                    }
                }
                
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to refresh $apiId: ${e.message}",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class ExtrasUiState(
    val catFact: CatFactData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
