package com.example.breathwatch.domain.repository

import com.example.breathwatch.data.remote.model.extras.*

interface ExtrasRepository {

    suspend fun getCatFact(): CatFactDto

    suspend fun getDogImage(): DogImageDto

    suspend fun getTriviaQuestion(): TriviaQuestionDto

    suspend fun getPublicHolidays(year: Int, countryCode: String): List<PublicHolidayDto>

    suspend fun getUniversities(country: String): List<UniversityDto>

    suspend fun getBooks(query: String): BookDto

    suspend fun getBitcoinPrice(): BitcoinPriceDto

    suspend fun getSpaceBodies(): SpaceBodyDto
}
