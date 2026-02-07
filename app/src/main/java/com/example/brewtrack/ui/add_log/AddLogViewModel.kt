package com.example.brewtrack.ui.add_log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewtrack.model.CoffeeLog
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
    val pours: String = "3",
    val turbulence: TurbulenceType = TurbulenceType.NONE,
    val pourDetails: String = "",
    val contactTime: String = "180",
    val totalTime: String = "210",

    // Sensorial
    val acidity: Int? = null,
    val sweetness: Int? = null,
    val body: Int? = null,
    val aftertaste: Int? = null,

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

        _uiState.update {
            it.copy(
                coffeeInGrams = coffee,
                waterInMilliliters = water
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

    fun onPoursChange(newPours: String) {
        _uiState.update { it.copy(pours = newPours) }
    }

    fun onTurbulenceChange(newTurbulence: TurbulenceType) {
        _uiState.update { it.copy(turbulence = newTurbulence) }
    }

    fun onPourDetailsChange(newDetails: String) {
        _uiState.update { it.copy(pourDetails = newDetails) }
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
                pours = currentState.pours.toIntOrNull() ?: 0,
                turbulence = currentState.turbulence,
                pourDetails = currentState.pourDetails,
                contactTime = currentState.contactTime.toIntOrNull() ?: 0,
                totalTime = currentState.totalTime.toIntOrNull() ?: 0,
                acidity = currentState.acidity,
                sweetness = currentState.sweetness,
                body = currentState.body,
                aftertaste = currentState.aftertaste,
                rating = currentState.rating,
                date = System.currentTimeMillis(),
                notes = currentState.notes
            )
            coffeeLogRepository.insert(newLog)
            _uiState.update { it.copy(isLogSaved = true) }
        }
    }
}
