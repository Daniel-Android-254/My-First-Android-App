package com.example.breathwatch.data.local.dao

import androidx.room.*
import com.example.breathwatch.data.local.entity.CatFactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExtrasDao {
    @Query("SELECT * FROM cat_facts ORDER BY timestamp DESC LIMIT 1")
    fun observeLatestCatFact(): Flow<CatFactEntity?>

    @Query("SELECT * FROM cat_facts ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestCatFact(): CatFactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCatFact(catFact: CatFactEntity)

    @Query("DELETE FROM cat_facts WHERE timestamp < :olderThan")
    suspend fun deleteOldCatFacts(olderThan: Long): Int

    @Query("DELETE FROM cat_facts")
    suspend fun deleteAllCatFacts()

    @Query("SELECT COUNT(*) FROM cat_facts")
    suspend fun getCatFactsCount(): Int
}
