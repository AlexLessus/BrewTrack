package com.example.brewtrack.model

data class PourStep(
    val phase: String,
    val time: String,
    val waterAdded: Float,
    val totalWeight: Float
)
