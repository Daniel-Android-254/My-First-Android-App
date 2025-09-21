package com.example.breathwatch.data.remote.api.extras

import com.example.breathwatch.data.remote.model.extras.PublicHolidayDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PublicHolidayApi {

    @GET("PublicHolidays/{year}/{countryCode}")
    suspend fun getPublicHolidays(
        @Query("year") year: Int,
        @Query("countryCode") countryCode: String
    ): List<PublicHolidayDto>
}
