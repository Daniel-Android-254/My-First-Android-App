package com.example.breathwatch.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.breathwatch.domain.model.CatFactData

@Entity(tableName = "cat_facts")
data class CatFactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fact: String,
    val timestamp: Long
) {
    fun toCatFactData() = CatFactData(
        fact = fact,
        timestamp = timestamp
    )
}
