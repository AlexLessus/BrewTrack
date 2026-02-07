package com.example.brewtrack.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeLogDao {

    @Insert
    suspend fun insert(coffeeLog: CoffeeLog)

    @Update
    suspend fun update(coffeeLog: CoffeeLog)

    @Delete
    suspend fun delete(coffeeLog: CoffeeLog)

    @Query("SELECT * FROM coffee_logs ORDER BY date DESC")
    fun getAll(): Flow<List<CoffeeLog>>

    @Query("SELECT * FROM coffee_logs WHERE id = :id")
    fun getById(id: Long): Flow<CoffeeLog>

    // 1. "Ver solo mis cafés de 5 estrellas"
    @Query("SELECT * FROM coffee_logs WHERE rating >= :minRating ORDER BY date DESC")
    fun getLogsByRating(minRating: Int): Flow<List<CoffeeLog>>

    // 2. "Repetir receta"
    @Query("SELECT * FROM coffee_logs WHERE method = :methodName ORDER BY date DESC")
    fun getLogsByMethod(methodName: String): Flow<List<CoffeeLog>>

    // 3. Búsqueda por grano
    @Query("SELECT * FROM coffee_logs WHERE origin LIKE '%' || :searchQuery || '%' ORDER BY date DESC")
    fun searchLogsByOrigin(searchQuery: String): Flow<List<CoffeeLog>>
}
