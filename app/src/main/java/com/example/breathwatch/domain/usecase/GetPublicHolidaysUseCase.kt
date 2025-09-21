package com.example.breathwatch.domain.usecase

import com.example.breathwatch.domain.repository.ExtrasRepository
import javax.inject.Inject

class GetPublicHolidaysUseCase @Inject constructor(
    private val extrasRepository: ExtrasRepository
) {
    suspend operator fun invoke(year: Int, countryCode: String) = extrasRepository.getPublicHolidays(year, countryCode)
}
