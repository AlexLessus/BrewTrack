package com.example.brewtrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "coffee_logs")
data class CoffeeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val beanName: String,
    val method: String,
    val ratio: Float,
    val water: Float,
    val coffee: Float,
    val rating: Int,
    val date: Date
)
