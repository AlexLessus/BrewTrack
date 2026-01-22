package com.example.brewtrack.ui.add_log

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("New Entry", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                navigationIcon = {
                    IconButton(onClick = { onLogSaved() /* O navegación hacia atrás */ }) {
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // --- Sección 1: Detalles del Grano ---
            SectionHeader(title = "The Bean", icon = Icons.Rounded.Grain)

            OutlinedTextField(
                value = uiState.beanName,
                onValueChange = viewModel::onBeanNameChange,
                label = { Text("Bean Name / Origin") },
                placeholder = { Text("e.g. Ethiopia Yirgacheffe") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = coffeeInputColors(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                leadingIcon = { Icon(Icons.Rounded.Coffee, null, tint = MaterialTheme.colorScheme.secondary) }
            )

            BrewMethodSelector(
                selectedMethod = uiState.method,
                onMethodSelected = viewModel::onMethodChange
            )

            // --- Sección 2: La Receta (Inputs compactos) ---
            SectionHeader(title = "The Recipe", icon = Icons.Rounded.Science)

            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CompactNumericInput(
                        value = "${uiState.coffeeAmount}",
                        onValueChange = { /* viewModel.onCoffeeChange(it) */ },
                        label = "Coffee",
                        suffix = "g",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CompactNumericInput(
                        value = "${uiState.waterAmount}",
                        onValueChange = { /* viewModel.onWaterChange(it) */ },
                        label = "Water",
                        suffix = "ml",
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CompactNumericInput(
                        value = "1:${uiState.ratio}",
                        onValueChange = {},
                        label = "Ratio",
                        suffix = "",
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // --- Sección 3: Resultados ---
            SectionHeader(title = "Experience", icon = Icons.Rounded.Star)

            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Overall Rating", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
                    Spacer(Modifier.height(8.dp))

                    InteractiveStarRating(
                        rating = uiState.rating,
                        onRatingChange = viewModel::onRatingChange
                    )

                    Spacer(Modifier.height(16.dp))
                    Divider(color = MaterialTheme.colorScheme.background)
                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = uiState.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text("Tasting Notes") },
                        placeholder = { Text("Fruity, acidic, chocolatey...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = coffeeInputColors(),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }
            }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun SectionHeader(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun BrewMethodSelector(
    selectedMethod: String,
    onMethodSelected: (String) -> Unit
) {
    val methods = listOf("V60", "AeroPress", "French Press", "Chemex", "Espresso")
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedMethod,
            onValueChange = {},
            readOnly = true,
            label = { Text("Brew Method") },
            leadingIcon = { Icon(Icons.Rounded.FilterList, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = coffeeInputColors()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface)
        ) {
            methods.forEach { method ->
                DropdownMenuItem(
                    text = { Text(method, color = MaterialTheme.colorScheme.primary) },
                    onClick = {
                        onMethodSelected(method)
                        expanded = false
                    }
                )
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
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
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
                unfocusedBorderColor = Color.Transparent,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
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
    focusedContainerColor = Color.White
)

@Preview(showBackground = true)
@Composable
fun AddLogScreenPreview() {
    BrewTrackTheme {
        AddLogScreen(onLogSaved = {})
    }
}
