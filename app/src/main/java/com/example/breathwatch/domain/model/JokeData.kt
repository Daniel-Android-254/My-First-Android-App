package com.example.breathwatch.domain.model

import java.time.LocalDateTime

data class JokeData(
    val id: Int,
    val text: String,
    val category: String,
    val type: String, // "single" or "twopart"
    val isSafe: Boolean,
    val lastUpdated: LocalDateTime,
    val isStale: Boolean = false
) {
    val displayText: String
        get() = text.replace("\\n", "\n")
}
