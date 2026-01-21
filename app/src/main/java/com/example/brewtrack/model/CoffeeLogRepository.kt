package com.example.brewtrack.model

import kotlinx.coroutines.flow.Flow

class CoffeeLogRepository(private val coffeeLogDao: CoffeeLogDao) {

    fun getAll(): Flow<List<CoffeeLog>> = coffeeLogDao.getAll()

    fun getById(id: Long): Flow<CoffeeLog> = coffeeLogDao.getById(id)

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
