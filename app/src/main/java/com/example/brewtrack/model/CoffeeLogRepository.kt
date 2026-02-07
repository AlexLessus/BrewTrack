package com.example.brewtrack.model

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject // Necesario para Hilt

class CoffeeLogRepository @Inject constructor(
    private val coffeeLogDao: CoffeeLogDao
) {
    fun getAll(): Flow<List<CoffeeLog>> = coffeeLogDao.getAll()

    fun getById(id: Long): Flow<CoffeeLog> = coffeeLogDao.getById(id)

    suspend fun getLogById(id: Long): CoffeeLog? = coffeeLogDao.getLogById(id)

    fun getLogsByRating(rating: Int): Flow<List<CoffeeLog>> =
        coffeeLogDao.getLogsByRating(rating)

    fun getLogsByMethod(method: String): Flow<List<CoffeeLog>> =
        coffeeLogDao.getLogsByMethod(method)

    fun searchLogs(query: String): Flow<List<CoffeeLog>> =
        coffeeLogDao.searchLogsByOrigin(query)

    suspend fun getLatestLog(): CoffeeLog? = coffeeLogDao.getLatestLog()

    suspend fun insert(coffeeLog: CoffeeLog) {
        coffeeLogDao.insert(coffeeLog)
    }

    suspend fun update(coffeeLog: CoffeeLog) {
        coffeeLogDao.update(coffeeLog)
    }

    suspend fun delete(coffeeLog: CoffeeLog) {
        coffeeLogDao.delete(coffeeLog)
    }
}
