package com.example.breathwatch.ui.screens.fun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.JokeData
import com.example.breathwatch.domain.model.QuoteData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FunViewModel @Inject constructor(
    private val getJokeUseCase: GetJokeUseCase,
    private val getQuoteUseCase: GetQuoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FunUiState())
    val uiState: StateFlow<FunUiState> = _uiState.asStateFlow()

    fun loadInitialContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // TODO: Replace with actual API calls when use cases are implemented
                // For now, using mock data to demonstrate the UI
                val mockJoke = JokeData(
                    id = 1,
                    text = "Why don't scientists trust atoms? Because they make up everything!",
                    category = "Programming",
                    type = "single",
                    isSafe = true,
                    lastUpdated = LocalDateTime.now()
                )
                
                val mockQuote = QuoteData(
                    text = "The only way to do great work is to love what you do.",
                    author = "Steve Jobs",
                    length = 52,
                    lastUpdated = LocalDateTime.now()
                )
                
                _uiState.value = _uiState.value.copy(
                    joke = mockJoke,
                    quote = mockQuote,
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

    fun refreshContent() {
        loadInitialContent()
    }

    fun refreshJoke() {
        viewModelScope.launch {
            try {
                // TODO: Replace with actual API call when use case is implemented
                val mockJoke = JokeData(
                    id = 2,
                    text = "Why do programmers prefer dark mode? Because light attracts bugs!",
                    category = "Programming",
                    type = "single",
                    isSafe = true,
                    lastUpdated = LocalDateTime.now()
                )
                
                _uiState.value = _uiState.value.copy(joke = mockJoke)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to refresh joke: ${e.message}"
                )
            }
        }
    }

    fun refreshQuote() {
        viewModelScope.launch {
            try {
                // TODO: Replace with actual API call when use case is implemented
                val mockQuote = QuoteData(
                    text = "Innovation distinguishes between a leader and a follower.",
                    author = "Steve Jobs",
                    length = 58,
                    lastUpdated = LocalDateTime.now()
                )
                
                _uiState.value = _uiState.value.copy(quote = mockQuote)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to refresh quote: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class FunUiState(
    val joke: JokeData? = null,
    val quote: QuoteData? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
