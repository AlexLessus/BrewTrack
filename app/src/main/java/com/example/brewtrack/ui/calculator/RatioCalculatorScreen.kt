package com.example.brewtrack.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.brewtrack.ui.theme.BrewTrackTheme

@Composable
fun RatioCalculatorScreen(
    viewModel: RatioCalculatorViewModel = hiltViewModel()
) {
    val waterAmount by viewModel.waterAmount.collectAsStateWithLifecycle()
    val coffeeAmount by viewModel.coffeeAmount.collectAsStateWithLifecycle()
    val ratio by viewModel.ratio.collectAsStateWithLifecycle()

    RatioCalculatorContent(
        waterAmount = waterAmount,
        onWaterAmountChange = viewModel::onWaterAmountChange,
        coffeeAmount = coffeeAmount,
        onCoffeeAmountChange = viewModel::onCoffeeAmountChange,
        ratio = ratio,
        onRatioChange = viewModel::onRatioChange
    )
}

@Composable
fun RatioCalculatorContent(
    waterAmount: String,
    onWaterAmountChange: (String) -> Unit,
    coffeeAmount: String,
    onCoffeeAmountChange: (String) -> Unit,
    ratio: String,
    onRatioChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Coffee Ratio Calculator", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = waterAmount,
            onValueChange = onWaterAmountChange,
            label = { Text("Water (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = coffeeAmount,
            onValueChange = onCoffeeAmountChange,
            label = { Text("Coffee (g)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "Ratio: 1:${"%.1f".format(ratio.toFloatOrNull() ?: 0f)}",
            style = MaterialTheme.typography.titleMedium
        )

        Slider(
            value = ratio.toFloatOrNull() ?: 16f,
            onValueChange = { onRatioChange(String.format("%.1f", it)) },
            valueRange = 1f..30f,
            steps = 289,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ratio,
            onValueChange = onRatioChange,
            label = { Text("Precise Ratio") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RatioCalculatorScreenPreview() {
    BrewTrackTheme {
        RatioCalculatorContent(
            waterAmount = "250",
            onWaterAmountChange = {},
            coffeeAmount = "15.6",
            onCoffeeAmountChange = {},
            ratio = "16.0",
            onRatioChange = {}
        )
    }
}
