package com.example.brewtrack.ui.add_log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewtrack.model.CoffeeLog
import com.example.brewtrack.model.PourStep
import com.example.brewtrack.model.TurbulenceType
import com.example.brewtrack.model.CoffeeLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject

data class AddLogUiState(
    // Insumos
    val origin: String = "",
    val process: String = "",
    val roast: String = "",

    // Extracción
    val method: String = "V60",
    val ratio: Float = 15.0f,
    val coffeeInGrams: String = "20",
    val waterInMilliliters: String = "300",
    val waterTemperature: String = "93",
    val grindSize: String = "20",

    // Técnica
    val bloomTime: String = "45",
    val numberOfPours: Int = 3,
    val recipeSteps: List<PourStep> = emptyList(),
    val turbulence: TurbulenceType = TurbulenceType.NONE,
    val contactTime: String = "180",
    val totalTime: String = "210",

    // Sensorial
    val acidity: Int? = null,
    val sweetness: Int? = null,
    val body: Int? = null,
    val aftertaste: Int? = null,
    val bitterness: Int? = null,

    // General
    val rating: Int = 0,
    val notes: String = "",
    val isLogSaved: Boolean = false
)

@HiltViewModel
class AddLogViewModel @Inject constructor(
    private val coffeeLogRepository: CoffeeLogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddLogUiState())
    val uiState: StateFlow<AddLogUiState> = _uiState.asStateFlow()

    init {
        val coffee: String = savedStateHandle.get<String>("coffee") ?: "20"
        val water: String = savedStateHandle.get<String>("water") ?: "300"

        // Initialize with default steps
        val defaultPours = 3
        val initialSteps = generateSteps(defaultPours)

        _uiState.update {
            it.copy(
                coffeeInGrams = coffee,
                waterInMilliliters = water,
                numberOfPours = defaultPours,
                recipeSteps = initialSteps
            )
        }
        updateRatio()
    }

    fun onOriginChange(newOrigin: String) {
        _uiState.update { it.copy(origin = newOrigin) }
    }

    fun onProcessChange(newProcess: String) {
        _uiState.update { it.copy(process = newProcess) }
    }

    fun onRoastChange(newRoast: String) {
        _uiState.update { it.copy(roast = newRoast) }
    }

    fun onMethodChange(newMethod: String) {
        _uiState.update { it.copy(method = newMethod) }
    }

    fun onRatingChange(newRating: Int) {
        _uiState.update { it.copy(rating = newRating) }
    }

    fun onNotesChange(newNotes: String) {
        _uiState.update { it.copy(notes = newNotes) }
    }

    fun onCoffeeInGramsChange(newAmount: String) {
        _uiState.update { it.copy(coffeeInGrams = newAmount) }
        updateRatio()
    }

    fun onWaterInMillilitersChange(newAmount: String) {
        _uiState.update { it.copy(waterInMilliliters = newAmount) }
        updateRatio()
    }

    fun onWaterTemperatureChange(newTemp: String) {
        _uiState.update { it.copy(waterTemperature = newTemp) }
    }

    fun onGrindSizeChange(newSize: String) {
        _uiState.update { it.copy(grindSize = newSize) }
    }

    fun onBloomTimeChange(newTime: String) {
        _uiState.update { it.copy(bloomTime = newTime) }
    }

    fun onNumberOfPoursChange(newPours: Int) {
        if (newPours < 1) return
        val currentSteps = _uiState.value.recipeSteps
        val newSteps = adjustSteps(currentSteps, newPours)
        _uiState.update { it.copy(numberOfPours = newPours, recipeSteps = newSteps) }
    }

    fun onStepWaterChange(index: Int, newAmount: String) {
        val amount = newAmount.toFloatOrNull() ?: 0f
        val currentSteps = _uiState.value.recipeSteps.toMutableList()
        
        if (index in currentSteps.indices) {
            val oldStep = currentSteps[index]
            currentSteps[index] = oldStep.copy(waterAdded = amount)
            val updatedSteps = recalculateTotals(currentSteps)
            _uiState.update { it.copy(recipeSteps = updatedSteps) }
        }
    }

    fun onStepTimeChange(index: Int, newTime: String) {
        val currentSteps = _uiState.value.recipeSteps.toMutableList()
        if (index in currentSteps.indices) {
            currentSteps[index] = currentSteps[index].copy(time = newTime)
            _uiState.update { it.copy(recipeSteps = currentSteps) }
        }
    }

    private fun generateSteps(pours: Int): List<PourStep> {
        val steps = mutableListOf<PourStep>()
        // Fixed Bloom
        steps.add(PourStep("Bloom", "0:00", 0f, 0f))
        // Pours
        for (i in 1..pours) {
            steps.add(PourStep("Vertido $i", "", 0f, 0f))
        }
        return steps
    }

    private fun adjustSteps(current: List<PourStep>, newPours: Int): List<PourStep> {
        val targetSize = newPours + 1 // +1 for Bloom
        val newSteps = current.toMutableList()
        
        if (newSteps.size < targetSize) {
            // Add needed steps
            for (i in newSteps.size until targetSize) {
                // Determine phase name based on index
                // Index 0 is Bloom. Index 1 is Vertido 1.
                val name = "Vertido $i"
                newSteps.add(PourStep(name, "", 0f, 0f))
            }
        } else if (newSteps.size > targetSize) {
            // Remove excess
            while (newSteps.size > targetSize) {
                newSteps.removeAt(newSteps.lastIndex)
            }
        }
        // Always recalculate to be safe
        return recalculateTotals(newSteps)
    }

    private fun recalculateTotals(steps: List<PourStep>): List<PourStep> {
        var currentTotal = 0f
        return steps.map { step ->
            currentTotal += step.waterAdded
            step.copy(totalWeight = currentTotal)
        }
    }

    fun onTurbulenceChange(newTurbulence: TurbulenceType) {
        _uiState.update { it.copy(turbulence = newTurbulence) }
    }


    fun onContactTimeChange(newTime: String) {
        _uiState.update { it.copy(contactTime = newTime) }
    }

    fun onTotalTimeChange(newTime: String) {
        _uiState.update { it.copy(totalTime = newTime) }
    }

    fun onAcidityChange(value: Int) {
        _uiState.update { it.copy(acidity = value) }
    }

    fun onSweetnessChange(value: Int) {
        _uiState.update { it.copy(sweetness = value) }
    }

    fun onBodyChange(value: Int) {
        _uiState.update { it.copy(body = value) }
    }

    fun onAftertasteChange(value: Int) {
        _uiState.update { it.copy(aftertaste = value) }
    }

    fun onBitternessChange(value: Int) {
        _uiState.update { it.copy(bitterness = value) }
    }

    private fun updateRatio() {
        val coffee = _uiState.value.coffeeInGrams.toFloatOrNull()
        val water = _uiState.value.waterInMilliliters.toFloatOrNull()

        if (coffee != null && water != null && coffee > 0) {
            _uiState.update { it.copy(ratio = water / coffee) }
        } else {
            _uiState.update { it.copy(ratio = 0f) }
        }
    }

    fun saveLog() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val newLog = CoffeeLog(
                origin = currentState.origin,
                process = currentState.process,
                roast = currentState.roast,
                method = currentState.method,
                ratio = currentState.ratio,
                coffeeInGrams = currentState.coffeeInGrams.toFloatOrNull() ?: 0f,
                waterInMilliliters = currentState.waterInMilliliters.toFloatOrNull() ?: 0f,
                waterTemperature = currentState.waterTemperature.toIntOrNull() ?: 0,
                grindSize = currentState.grindSize.toIntOrNull() ?: 0,
                bloomTime = currentState.bloomTime.toIntOrNull() ?: 0,
                recipeSteps = currentState.recipeSteps,
                turbulence = currentState.turbulence,
                contactTime = currentState.contactTime.toIntOrNull() ?: 0,
                totalTime = currentState.totalTime.toIntOrNull() ?: 0,
                acidity = currentState.acidity,
                sweetness = currentState.sweetness,
                body = currentState.body,
                aftertaste = currentState.aftertaste,
                bitterness = currentState.bitterness,
                rating = currentState.rating,
                date = System.currentTimeMillis(),
                notes = currentState.notes
            )
            coffeeLogRepository.insert(newLog)
            _uiState.update { it.copy(isLogSaved = true) }
        }
    }
}
