package com.example.brewtrack.ui.calculator

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RatioCalculatorViewModel @Inject constructor() : ViewModel() {

    private val _waterAmount = MutableStateFlow("")
    val waterAmount: StateFlow<String> = _waterAmount

    private val _coffeeAmount = MutableStateFlow("")
    val coffeeAmount: StateFlow<String> = _coffeeAmount

    private val _ratio = MutableStateFlow("16.0")
    val ratio: StateFlow<String> = _ratio

    fun onWaterAmountChange(newAmount: String) {
        _waterAmount.value = newAmount
        val water = newAmount.toFloatOrNull()
        val r = _ratio.value.toFloatOrNull()

        if (water != null && r != null && r > 0) {
            _coffeeAmount.value = String.format("%.1f", water / r)
        } else if (newAmount.isEmpty()) {
            _coffeeAmount.value = ""
        }
    }

    fun onCoffeeAmountChange(newAmount: String) {
        _coffeeAmount.value = newAmount
        val coffee = newAmount.toFloatOrNull()
        val r = _ratio.value.toFloatOrNull()

        if (coffee != null && r != null) {
            _waterAmount.value = String.format("%.1f", coffee * r)
        } else if (newAmount.isEmpty()) {
            _waterAmount.value = ""
        }
    }

    fun onRatioChange(newRatio: String) {
        if (newRatio == ".") {
            _ratio.value = "0."
            return
        }
        _ratio.value = newRatio
        val coffee = _coffeeAmount.value.toFloatOrNull()
        val r = newRatio.toFloatOrNull()

        if (coffee != null && r != null) {
            _waterAmount.value = String.format("%.1f", coffee * r)
        } else if (newRatio.isEmpty()){
            _waterAmount.value = ""
        }
    }
}
