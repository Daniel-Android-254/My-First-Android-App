package com.example.breathwatch.domain.model

import java.time.LocalDateTime

data class CatFactData(
    val fact: String,
    val length: Int,
    val lastUpdated: LocalDateTime,
    val isStale: Boolean = false
) {
    val displayText: String
        get() = fact
}
