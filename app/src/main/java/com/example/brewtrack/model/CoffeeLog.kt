package com.example.brewtrack.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class TurbulenceType {
    NONE,       // Solo vertido
    AGITATION,  // Usar cuchara/agitador
    SWIRL,      // Girar el dripper
    TOSS        // Movimiento brusco (menos común)
}

@Entity(tableName = "coffee_logs")
data class CoffeeLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // --- INSUMOS ---
    val origin: String,
    val process: String,
    val roast: String,

    // --- EXTRACCIÓN (Datos Técnicos) ---
    val method: String,            // Ej: "V60", "Aeropress"
    val ratio: Float,              // Ej: 15.0
    val coffeeInGrams: Float,      // Ej: 20.0
    val waterInMilliliters: Float, // Ej: 300.0
    val waterTemperature: Int,     // Celsius
    val grindSize: Int,            // Clicks

    // --- TÉCNICA ---
    val bloomTime: Int,            // Segundos
    val recipeSteps: List<PourStep> = emptyList(), // Pasos de la receta
    val turbulence: TurbulenceType,// Turbulencia (FA o FE)
    val contactTime: Int,          // Tiempo de contacto (segundos)
    val totalTime: Int,            // Tiempo total (segundos)

    // --- PERFIL SENSORIAL (Escala 1-5) ---
    val acidity: Int? = null,
    val sweetness: Int? = null,
    val body: Int? = null,
    val aftertaste: Int? = null,
    val bitterness: Int? = null,

    // --- GENERAL ---
    val rating: Int,               // Calificación general (1-5)
    val date: Long,                // Timestamp (System.currentTimeMillis())
    val notes: String = ""
)