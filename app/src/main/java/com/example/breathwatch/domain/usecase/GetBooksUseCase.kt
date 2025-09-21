package com.example.breathwatch.domain.usecase

import com.example.breathwatch.domain.repository.ExtrasRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val extrasRepository: ExtrasRepository
) {
    suspend operator fun invoke(query: String) = extrasRepository.getBooks(query)
}
