package com.example.breathwatch.domain.model

import java.time.LocalDateTime

data class QuoteData(
    val text: String,
    val author: String,
    val length: Int,
    val lastUpdated: LocalDateTime,
    val isStale: Boolean = false
) {
    val displayText: String
        get() = "\"$text\""
    
    val displayAuthor: String
        get() = "- $author"
}
