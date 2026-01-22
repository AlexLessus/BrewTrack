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
    val coffeeAmount: Float = 0f,
    val waterAmount: Float = 0f,
    val ratio: Float = 0f,
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
        // For now, let's assume these are passed as nav arguments
        val coffee: Float = savedStateHandle.get<String>("coffee")?.toFloatOrNull() ?: 15f
        val water: Float = savedStateHandle.get<String>("water")?.toFloatOrNull() ?: 250f
        val ratio: Float = savedStateHandle.get<String>("ratio")?.toFloatOrNull() ?: 16f

        _uiState.update {
            it.copy(
                coffeeAmount = coffee,
                waterAmount = water,
                ratio = ratio
            )
        }
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

    fun saveLog() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val newLog = CoffeeLog(
                beanName = currentState.beanName,
                method = currentState.method,
                rating = currentState.rating,
                date = Date(),
                coffee = currentState.coffeeAmount,
                water = currentState.waterAmount,
                ratio = currentState.ratio,
                notes = currentState.notes
            )
            coffeeLogRepository.insert(newLog)
            _uiState.update { it.copy(isLogSaved = true) }
        }
    }
}
