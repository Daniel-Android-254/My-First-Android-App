package com.example.breathwatch.ui.screens.extras

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.breathwatch.domain.model.CatFactData
import com.example.breathwatch.data.remote.model.extras.*
import com.example.breathwatch.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ExtrasViewModel @Inject constructor(
    private val getCatFactUseCase: GetCatFactUseCase,
    private val getDogImageUseCase: GetDogImageUseCase,
    private val getTriviaQuestionUseCase: GetTriviaQuestionUseCase,
    private val getPublicHolidaysUseCase: GetPublicHolidaysUseCase,
    private val getUniversitiesUseCase: GetUniversitiesUseCase,
    private val getBooksUseCase: GetBooksUseCase,
    private val getBitcoinPriceUseCase: GetBitcoinPriceUseCase,
    private val getSpaceBodiesUseCase: GetSpaceBodiesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExtrasUiState())
    val uiState: StateFlow<ExtrasUiState> = _uiState.asStateFlow()

    fun loadInitialContent() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                val catFact = getCatFactUseCase()
                _uiState.value = _uiState.value.copy(
                    catFact = CatFactData(
                        fact = catFact.fact,
                        length = catFact.length,
                        lastUpdated = LocalDateTime.now()
                    ),
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
                        val catFact = getCatFactUseCase()
                        _uiState.value = _uiState.value.copy(catFact = CatFactData(
                            fact = catFact.fact,
                            length = catFact.length,
                            lastUpdated = LocalDateTime.now()
                        ))
                    }
                    "dogfacts" -> {
                        val dogImage = getDogImageUseCase()
                        _uiState.value = _uiState.value.copy(dogImage = dogImage)
                    }
                    "trivia" -> {
                        val triviaQuestion = getTriviaQuestionUseCase()
                        _uiState.value = _uiState.value.copy(triviaQuestion = triviaQuestion)
                    }
                    "holidays" -> {
                        val holidays = getPublicHolidaysUseCase(2024, "US")
                        _uiState.value = _uiState.value.copy(publicHolidays = holidays)
                    }
                    "universities" -> {
                        val universities = getUniversitiesUseCase("United States")
                        _uiState.value = _uiState.value.copy(universities = universities)
                    }
                    "books" -> {
                        val books = getBooksUseCase("science")
                        _uiState.value = _uiState.value.copy(book = books)
                    }
                    "bitcoin" -> {
                        val bitcoinPrice = getBitcoinPriceUseCase()
                        _uiState.value = _uiState.value.copy(bitcoinPrice = bitcoinPrice)
                    }
                    "space" -> {
                        val spaceBody = getSpaceBodiesUseCase()
                        _uiState.value = _uiState.value.copy(spaceBody = spaceBody)
                    }
                    else -> {
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
    val dogImage: DogImageDto? = null,
    val triviaQuestion: TriviaQuestionDto? = null,
    val publicHolidays: List<PublicHolidayDto>? = null,
    val universities: List<UniversityDto>? = null,
    val book: BookDto? = null,
    val bitcoinPrice: BitcoinPriceDto? = null,
    val spaceBody: SpaceBodyDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
