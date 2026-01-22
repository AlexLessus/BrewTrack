package com.example.brewtrack.ui.calculator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.brewtrack.ui.theme.BrewTrackTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatioCalculatorScreen(
    viewModel: RatioCalculatorViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val waterAmount by viewModel.waterAmount.collectAsStateWithLifecycle(lifecycleOwner)
    val coffeeAmount by viewModel.coffeeAmount.collectAsStateWithLifecycle(lifecycleOwner)
    val ratio by viewModel.ratio.collectAsStateWithLifecycle(lifecycleOwner)

    // Envolvemos el contenido en un Scaffold para la estructura MD3
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Brew Calculator",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Calculate, contentDescription = null) },
                    label = { Text("Calculator") },
                    selected = true,
                    onClick = { /* Navegación */ },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.tertiary
                    )
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Book, contentDescription = null) },
                    label = { Text("Journal") },
                    selected = false,
                    onClick = { /* Navegación */ },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedIconColor = Color.Gray
                    )
                )
            }
        }
    ) { innerPadding ->
        RatioCalculatorContent(
            waterAmount = waterAmount,
            onWaterAmountChange = viewModel::onWaterAmountChange,
            coffeeAmount = coffeeAmount,
            onCoffeeAmountChange = viewModel::onCoffeeAmountChange,
            ratio = ratio,
            onRatioChange = viewModel::onRatioChange,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun RatioCalculatorContent(
    waterAmount: String,
    onWaterAmountChange: (String) -> Unit,
    coffeeAmount: String,
    onCoffeeAmountChange: (String) -> Unit,
    ratio: String,
    onRatioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // Hacemos scrollable por si pantallas pequeñas
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // --- 1. Large Display Area (Resultado Principal) ---
        ResultCard(ratio = ratio)

        // --- 2. Interactive Controls ---
        Text(
            text = "Adjust Parameters",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        // Coffee Input
        CoffeeInputRow(
            value = coffeeAmount,
            onValueChange = onCoffeeAmountChange,
            label = "Coffee Grounds (g)",
            icon = Icons.Default.LocalCafe
        )

        // Water Input
        CoffeeInputRow(
            value = waterAmount,
            onValueChange = onWaterAmountChange,
            label = "Water (ml)",
            icon = Icons.Default.WaterDrop
        )

        // Ratio Slider & Input
        RatioControlSection(ratio = ratio, onRatioChange = onRatioChange)
    }
}

@Composable
fun ResultCard(ratio: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Target Ratio",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = "1:${"%.1f".format(ratio.toFloatOrNull() ?: 0f)}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 48.sp
                ),
                color = Color.White
            )
        }
    }
}

@Composable
fun CoffeeInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = Color.White
        ),
        singleLine = true
    )
}

@Composable
fun RatioControlSection(
    ratio: String,
    onRatioChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Strength", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
                Text(
                    text = "1:${"%.1f".format(ratio.toFloatOrNull() ?: 0f)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Slider(
                value = ratio.toFloatOrNull() ?: 16f,
                onValueChange = { onRatioChange(String.format("%.1f", it)) },
                valueRange = 1f..30f,
                steps = 0, // Steps 0 hace que sea continuo y suave
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    inactiveTrackColor = Color.LightGray
                )
            )

            // Input manual pequeño para el ratio por si quieren exactitud
            OutlinedTextField(
                value = ratio,
                onValueChange = onRatioChange,
                label = { Text("Precise Ratio") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.secondary
                )
            )
        }
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
