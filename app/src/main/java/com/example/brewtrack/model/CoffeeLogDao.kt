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
}
