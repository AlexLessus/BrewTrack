package com.example.brewtrack.ui.add_log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewtrack.model.CoffeeLog
import com.example.brewtrack.model.CoffeeLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class AddLogUiState(
    val beanName: String = "",
    val method: String = "V60", // Default method
    val rating: Int = 0,
    val notes: String = "",
    val coffeeAmount: String = "15",
    val waterAmount: String = "250",
    val ratio: Float = 16.6f,
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
        val coffee: String = savedStateHandle.get<String>("coffee") ?: "15"
        val water: String = savedStateHandle.get<String>("water") ?: "250"

        _uiState.update {
            it.copy(
                coffeeAmount = coffee,
                waterAmount = water
            )
        }
        updateRatio()
    }

    fun onBeanNameChange(newName: String) {
        _uiState.update { it.copy(beanName = newName) }
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

    fun onCoffeeAmountChange(newAmount: String) {
        _uiState.update { it.copy(coffeeAmount = newAmount) }
        updateRatio()
    }

    fun onWaterAmountChange(newAmount: String) {
        _uiState.update { it.copy(waterAmount = newAmount) }
        updateRatio()
    }

    private fun updateRatio() {
        val coffee = _uiState.value.coffeeAmount.toFloatOrNull()
        val water = _uiState.value.waterAmount.toFloatOrNull()

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
                beanName = currentState.beanName,
                method = currentState.method,
                rating = currentState.rating,
                date = Date(),
                coffee = currentState.coffeeAmount.toFloatOrNull() ?: 0f,
                water = currentState.waterAmount.toFloatOrNull() ?: 0f,
                ratio = currentState.ratio,
                notes = currentState.notes
            )
            coffeeLogRepository.insert(newLog)
            _uiState.update { it.copy(isLogSaved = true) }
        }
    }
}
