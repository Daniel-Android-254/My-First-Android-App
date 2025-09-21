package com.example.breathwatch.data.repository

import com.example.breathwatch.data.remote.api.extras.*
import com.example.breathwatch.data.remote.model.extras.*
import com.example.breathwatch.domain.repository.ExtrasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExtrasRepositoryImpl @Inject constructor(
    private val extrasApi: ExtrasApi,
    private val cacheStrategy: CacheStrategy,
    private val extrasDao: ExtrasDao
) : ExtrasRepository, BaseRepository<CatFactData> {

    override suspend fun getCatFact(): Result<CatFactData> = try {
        cacheStrategy.cachingStrategy(
            dbQuery = { extrasDao.getLatestCatFact() },
            networkCall = {
                Result.wrap {
                    extrasApi.getCatFact().toCatFactData()
                }
            },
            saveCallResult = { extrasDao.insertCatFact(it) },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, CACHE_TIMEOUT)
            }
        ).first()
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun refresh() = try {
        val response = extrasApi.getCatFact()
        val catFact = response.toCatFactData()
        extrasDao.insertCatFact(catFact)
        Result.Success(catFact)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override fun observe(): Flow<Result<CatFactData>> {
        return cacheStrategy.cachingStrategy(
            dbQuery = { extrasDao.observeLatestCatFact() },
            networkCall = { refresh() },
            saveCallResult = { /* Already saved in refresh() */ },
            shouldFetch = { cached ->
                cached == null || isDataStale(cached.timestamp, CACHE_TIMEOUT)
            }
        )
    }

    override suspend fun get(): Result<CatFactData> = observe().first()

    override suspend fun clear() {
        extrasDao.deleteAllCatFacts()
    }

    companion object {
        private const val CACHE_TIMEOUT = 30 * 60 * 1000L // 30 minutes
    }
}
