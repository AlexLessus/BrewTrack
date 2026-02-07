package com.example.brewtrack.ui.add_log

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.brewtrack.model.TurbulenceType
import com.example.brewtrack.model.PourStep
import com.example.brewtrack.ui.theme.BrewTrackTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLogScreen(
    viewModel: AddLogViewModel = hiltViewModel(),
    onLogSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // Manejo del evento de guardado
    LaunchedEffect(Unit) {
        viewModel.uiState.collectLatest {
            if (it.isLogSaved) {
                onLogSaved()
            }
        }
    }

    if (uiState.showReuseDialog) {
        AlertDialog(
            onDismissRequest = viewModel::onReuseDialogDismiss,
            title = { Text(text = "Reuse Previous Info?") },
            text = { 
                Column {
                    Text("Do you want to use the details from your last brew?")
                    Spacer(Modifier.height(8.dp))
                    Text("• Bean: ${uiState.lastLogData?.origin}")
                    Text("• Process: ${uiState.lastLogData?.process}")
                    Text("• Roast: ${uiState.lastLogData?.roast}")
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::onReuseDialogConfirm) {
                    Text("Yes, Reuse")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::onReuseDialogDismiss) {
                    Text("No, Start Fresh")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        if (uiState.currentLogId != null) "Edit Entry" else "New Entry", 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { onLogSaved() }) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel", tint = MaterialTheme.colorScheme.secondary)
                    }
                },
                actions = {
                    Button(
                        onClick = viewModel::saveLog,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. RESUMEN (Grano, Método, Ratio) ---
            ExpandableCard(
                title = "Summary",
                icon = Icons.Rounded.Grain,
                initialExpanded = true
            ) {
                // Bean Info
                OutlinedTextField(
                    value = uiState.origin,
                    onValueChange = viewModel::onOriginChange,
                    label = { Text("Origin / Bean Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = coffeeInputColors(),
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.process,
                        onValueChange = viewModel::onProcessChange,
                        label = { Text("Process") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = coffeeInputColors(),
                        singleLine = true
                    )
                }
                Spacer(Modifier.height(16.dp))

                // Roast Selection
                Text("Roast Level", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                SingleChoiceSegmentedRow(
                    options = listOf("Light", "Medium", "Dark"),
                    selectedOption = uiState.roast,
                    onOptionSelected = viewModel::onRoastChange
                )
                Spacer(Modifier.height(16.dp))

                // Method Selection
                Text("Brew Method", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                FlowRowGeneric(
                    items = listOf("V60", "Kalita", "Origami", "Chemex", "Aeropress", "French Press"),
                    selectedItem = uiState.method,
                    onItemSelected = viewModel::onMethodChange
                )
                Spacer(Modifier.height(16.dp))

                // Ratio & Amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactNumericInput(
                        value = uiState.coffeeInGrams,
                        onValueChange = viewModel::onCoffeeInGramsChange,
                        label = "Coffee",
                        suffix = "g",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CompactNumericInput(
                        value = uiState.waterInMilliliters,
                        onValueChange = viewModel::onWaterInMillilitersChange,
                        label = "Water",
                        suffix = "ml",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp) // Match height roughly
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Ratio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            Text("1:${String.format("%.1f", uiState.ratio)}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }

            // --- 2. TÉCNICA (Molienda, Temp, Tiempos, Vertidos) ---
            ExpandableCard(
                title = "Technique",
                icon = Icons.Rounded.Science,
                initialExpanded = false
            ) {
                // Grind Size Stepper
                StepperInput(
                    label = "Grind Size (Clicks)",
                    value = uiState.grindSize,
                    onValueChange = viewModel::onGrindSizeChange
                )
                Spacer(Modifier.height(16.dp))

                // Pours Stepper
                // Recipe Builder (Dynamic Pours)
                RecipeBuilderTable(
                    numberOfPours = uiState.numberOfPours,
                    steps = uiState.recipeSteps,
                    onPoursChange = viewModel::onNumberOfPoursChange,
                    onStepWaterChange = viewModel::onStepWaterChange,
                    onStepTimeChange = viewModel::onStepTimeChange
                )
                Spacer(Modifier.height(16.dp))
                
                // Temp
                CompactNumericInput(
                     value = uiState.waterTemperature,
                     onValueChange = viewModel::onWaterTemperatureChange,
                     label = "Water Temp",
                     suffix = "°C",
                     modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // Turbulence
                Text("Turbulence", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                FlowRowGeneric(
                    items = TurbulenceType.values().map { it.name },
                    selectedItem = uiState.turbulence.name,
                    onItemSelected = { viewModel.onTurbulenceChange(TurbulenceType.valueOf(it)) }
                )
                Spacer(Modifier.height(16.dp))

                // Times (Bloom, Total)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CompactNumericInput(
                        value = uiState.bloomTime,
                        onValueChange = viewModel::onBloomTimeChange,
                        label = "Bloom",
                        suffix = "s",
                        modifier = Modifier.weight(1f)
                    )
                    CompactNumericInput(
                        value = uiState.totalTime,
                        onValueChange = viewModel::onTotalTimeChange,
                        label = "Total Time",
                        suffix = "s",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- 3. CATA (Sensorial, Estrellas, Notas) ---
            ExpandableCard(
                title = "Tasting",
                icon = Icons.Rounded.Star,
                initialExpanded = true
            ) {
                SensorySlider(
                    label = "Acidity",
                    value = uiState.acidity,
                    onValueChange = viewModel::onAcidityChange,
                    leftLabel = "Flat",
                    rightLabel = "Bright"
                )
                Spacer(Modifier.height(12.dp))

                SensorySlider(
                    label = "Sweetness",
                    value = uiState.sweetness,
                    onValueChange = viewModel::onSweetnessChange,
                    leftLabel = "Dry",
                    rightLabel = "Sweet"
                )
                Spacer(Modifier.height(12.dp))

                SensorySlider(
                    label = "Body",
                    value = uiState.body,
                    onValueChange = viewModel::onBodyChange,
                    leftLabel = "Watery",
                    rightLabel = "Syrupy"
                )
                Spacer(Modifier.height(12.dp))

                SensorySlider(
                    label = "Aftertaste",
                    value = uiState.aftertaste,
                    onValueChange = viewModel::onAftertasteChange,
                    leftLabel = "Short",
                    rightLabel = "Lingering"
                )
                Spacer(Modifier.height(12.dp))

                SensorySlider(
                    label = "Bitterness",
                    value = uiState.bitterness,
                    onValueChange = viewModel::onBitternessChange,
                    leftLabel = "None",
                    rightLabel = "Harsh"
                )
                Spacer(Modifier.height(24.dp))
                
                Divider()
                Spacer(Modifier.height(16.dp))

                Text("Check-in Rating", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.align(Alignment.CenterHorizontally))
                InteractiveStarRating(
                    rating = uiState.rating,
                    onRatingChange = viewModel::onRatingChange
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChange,
                    label = { Text("Tasting Notes") },
                    placeholder = { Text("Fruity, acidic, chocolatey...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = coffeeInputColors(),
                    maxLines = 5
                )
            }
        }
    }
}

// --- CORE COMPONENTS ---

@Composable
fun ExpandableCard(
    title: String,
    icon: ImageVector,
    initialExpanded: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initialExpanded) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    content = content
                )
            }
        }
    }
}

@Composable
fun SingleChoiceSegmentedRow(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            val isSelected = option.equals(selectedOption, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = { onOptionSelected(option) },
                label = { Text(option) },
                modifier = Modifier.padding(end = 8.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowGeneric(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { item ->
            val isSelected = item.equals(selectedItem, ignoreCase = true)
            FilterChip(
                selected = isSelected,
                onClick = { onItemSelected(item) },
                label = { Text(item) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}

@Composable
fun SensorySlider(
    label: String,
    value: Int?,
    onValueChange: (Int) -> Unit,
    leftLabel: String,
    rightLabel: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text(
                text = value?.toString() ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = (value ?: 3).toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 1f..5f,
            steps = 3,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.secondary,
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(leftLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
            Text(rightLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun StepperInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val intValue = value.toIntOrNull() ?: 0

    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(bottom = 4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                .padding(4.dp)
        ) {
            IconButton(
                onClick = { if (intValue > 0) onValueChange((intValue - 1).toString()) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Filled.Remove, null, tint = MaterialTheme.colorScheme.primary)
            }
            
            Text(
                text = value,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { onValueChange((intValue + 1).toString()) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun CompactNumericInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    suffix: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 12.sp) },
            suffix = { Text(suffix) },
            singleLine = true,
            readOnly = readOnly,
            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun InteractiveStarRating(
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating) Icons.Rounded.Star else Icons.Rounded.StarOutline,
                contentDescription = "Star ${index + 1}",
                tint = if (index < rating) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onRatingChange(index + 1) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun coffeeInputColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedContainerColor = MaterialTheme.colorScheme.surface
)

@Preview(showBackground = true)
@Composable
fun AddLogScreenPreview() {
    BrewTrackTheme {
        AddLogScreen(onLogSaved = {})
    }
}

@Composable
fun RecipeBuilderTable(
    numberOfPours: Int,
    steps: List<PourStep>,
    onPoursChange: (Int) -> Unit,
    onStepWaterChange: (Int, String) -> Unit,
    onStepTimeChange: (Int, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. Selector de Pours
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Recipe Steps", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f))
                    .padding(2.dp)
            ) {
                IconButton(
                    onClick = { if (numberOfPours > 1) onPoursChange(numberOfPours - 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Filled.Remove, null, tint = MaterialTheme.colorScheme.primary)
                }
                
                Text(
                    text = "$numberOfPours Pours",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                
                IconButton(
                    onClick = { onPoursChange(numberOfPours + 1) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Filled.Add, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // 2. Header de la Tabla
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Phase", modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("Time", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("H2O (g)", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("Total", modifier = Modifier.weight(0.8f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }

        // 3. Filas
        steps.forEachIndexed { index, step ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Phase Name
                Text(
                    text = step.phase,
                    modifier = Modifier.weight(1.2f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )

                // Time Input
                BasicCompactInput(
                    value = step.time,
                    onValueChange = { onStepTimeChange(index, it) },
                    placeholder = "0:00",
                    modifier = Modifier.weight(0.8f)
                )

                // Water Input
                // Si es 0 y no es Bloom, mostrar vacío para que el usuario escriba. Bloom arranca en 0?
                // El usuario escribe cuanto agrega.
                val displayWater = if (step.waterAdded == 0f) "" else step.waterAdded.toString().removeSuffix(".0")
                BasicCompactInput(
                    value = displayWater,
                    onValueChange = { onStepWaterChange(index, it) },
                    placeholder = "0",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(0.8f)
                )

                // Total (Calculated)
                Text(
                    text = "${step.totalWeight.toInt()}g",
                    modifier = Modifier.weight(0.8f),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        }
    }
}

@Composable
fun BasicCompactInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 10.sp, color = Color.Gray) },
        modifier = modifier.height(48.dp), // Fixed height to align rows
        textStyle = MaterialTheme.typography.bodySmall,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = ImeAction.Next),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(8.dp)
    )
}
