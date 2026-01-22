package com.example.brewtrack.ui.brews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewtrack.model.CoffeeLog
import com.example.brewtrack.model.CoffeeLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BrewsViewModel @Inject constructor(
    coffeeLogRepository: CoffeeLogRepository
) : ViewModel() {

    val brews: StateFlow<List<CoffeeLog>> = coffeeLogRepository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
