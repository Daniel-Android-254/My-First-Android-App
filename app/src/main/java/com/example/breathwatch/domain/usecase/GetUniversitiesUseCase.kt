package com.example.breathwatch.domain.usecase

import com.example.breathwatch.domain.repository.ExtrasRepository
import javax.inject.Inject

class GetUniversitiesUseCase @Inject constructor(
    private val extrasRepository: ExtrasRepository
) {
    suspend operator fun invoke(country: String) = extrasRepository.getUniversities(country)
}
