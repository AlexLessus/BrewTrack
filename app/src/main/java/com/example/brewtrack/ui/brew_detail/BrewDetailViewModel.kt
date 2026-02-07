package com.example.brewtrack.ui.brew_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewtrack.model.CoffeeLog
import com.example.brewtrack.model.CoffeeLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrewDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coffeeLogRepository: CoffeeLogRepository
) : ViewModel() {

    private val logId: Long = checkNotNull(savedStateHandle["logId"])

    val brew: StateFlow<CoffeeLog?> = coffeeLogRepository.getById(logId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    fun deleteLog(onSuccess: () -> Unit) {
        viewModelScope.launch {
            brew.value?.let { 
                coffeeLogRepository.delete(it) 
                onSuccess()
            }
        }
    }
}
