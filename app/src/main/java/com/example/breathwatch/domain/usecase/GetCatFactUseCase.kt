package com.example.breathwatch.domain.usecase

import com.example.breathwatch.domain.repository.ExtrasRepository
import javax.inject.Inject

class GetCatFactUseCase @Inject constructor(
    private val extrasRepository: ExtrasRepository
) {
    suspend operator fun invoke(): Result<CatFactData> = try {
        extrasRepository.getCatFact()
    } catch (e: Exception) {
        Result.Error(Exception("Failed to get cat fact: ${e.message}"))
    }
}
