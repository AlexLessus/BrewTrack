package com.example.brewtrack.ui.brew_detail

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.brewtrack.model.CoffeeLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewDetailScreen(
    viewModel: BrewDetailViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val brew by viewModel.brew.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("") }, // Empty title for Hero look
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        brew?.let { log ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // A. HERO HEADER
                HeroHeader(log)
                Spacer(modifier = Modifier.height(32.dp))

                // B. SENSORY CHART
                Text(
                    "Flavor Profile",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                CoffeeRadarChart(
                    acidity = log.acidity ?: 0,
                    sweetness = log.sweetness ?: 0,
                    body = log.body ?: 0,
                    aftertaste = log.aftertaste ?: 0,
                    modifier = Modifier.size(300.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))

                // C. TECHNICAL SHEET
                SectionTitle("Brew Stats")
                TechnicalGrid(log)
                Spacer(modifier = Modifier.height(32.dp))

                // D. RECIPE STEPS
                SectionTitle("Recipe Steps")
                RecipeSteps(log)
                Spacer(modifier = Modifier.height(32.dp))
                
                // Notes if any
                if(log.notes.isNotBlank()) {
                    SectionTitle("Notes")
                    Text(
                        text = log.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f), RoundedCornerShape(8.dp)).padding(16.dp)
                    )
                     Spacer(modifier = Modifier.height(32.dp))
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}

// --- A. HERO HEADER ---
@Composable
fun HeroHeader(log: CoffeeLog) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Star Rating
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Star, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(8.dp))
            Text(
                text = "${log.rating}/5",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(16.dp))
        
        // Bean Name & Origin
        Text(
            text = log.origin,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Text(
            text = log.process.ifBlank { "Unknown Process" } + " • " + log.roast + " Roast",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        
        // Date
        Text(
            text = formatDateFull(log.date),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha=0.7f)
        )
    }
}

// --- B. RADAR CHART ---
@Composable
fun CoffeeRadarChart(
    acidity: Int,
    sweetness: Int,
    body: Int,
    aftertaste: Int,
    bitterness: Int = 1, // Defaulting to 1 as requested or minimal
    modifier: Modifier = Modifier
) {
    // Normalizing 0 input to 1 for visual purposes if needed, strictly sticking to user request logic
    val data = listOf(
        acidity.coerceAtLeast(1), 
        sweetness.coerceAtLeast(1), 
        body.coerceAtLeast(1), 
        aftertaste.coerceAtLeast(1), 
        bitterness.coerceAtLeast(1)
    )
    val labels = listOf("Acidity", "Sweetness", "Body", "Aftertaste", "Bitterness")
    val maxValue = 5f
    
    val primaryColor = MaterialTheme.colorScheme.primary
    val labelColor = MaterialTheme.colorScheme.onSurface

    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension / 2 - 60f // Increased margin for labels
        val angleStep = (2 * Math.PI / data.size).toFloat()

        // 1. WEB (Guidelines 1-5)
        for (i in 1..5) {
            val currentRadius = radius * (i / maxValue)
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = currentRadius,
                center = center,
                style = Stroke(width = 2f)
            )
        }

        // 2. DATA POLYGON
        val path = Path()
        val points = mutableListOf<Offset>()
        
        data.forEachIndexed { index, value ->
            val angle = (index * angleStep) - (Math.PI / 2).toFloat() // -90 deg to start top
            val pointRadius = radius * (value / maxValue)
            
            val x = center.x + pointRadius * cos(angle)
            val y = center.y + pointRadius * sin(angle)
            
            points.add(Offset(x, y))

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            
            // Draw Labels (outside)
            val labelRadius = radius + 40f
            val labelX = center.x + labelRadius * cos(angle)
            val labelY = center.y + labelRadius * sin(angle)
            
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    labels[index],
                    labelX,
                    labelY,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        textSize = 32f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
        path.close()

        // Fill
        drawPath(
            path = path,
            color = primaryColor.copy(alpha = 0.2f),
        )
        // Stroke
        drawPath(
            path = path,
            color = primaryColor,
            style = Stroke(width = 4f)
        )
        
        // Dots
        points.forEach { point ->
            drawCircle(primaryColor, radius = 8f, center = point)
        }
    }
}

// --- C. TECHNICAL GRID ---
@Composable
fun TechnicalGrid(log: CoffeeLog) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Row 1
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TechItem(Icons.Rounded.Coffee, "Method", log.method)
                TechItem(Icons.Rounded.WaterDrop, "Ratio", "1:${String.format("%.1f", log.ratio)}")
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
            
            // Row 2
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TechItem(Icons.Rounded.Grain, "Grind", "${log.grindSize} clicks")
                TechItem(Icons.Rounded.Timer, "Time", formatTime(log.totalTime))
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
            
            // Row 3
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                TechItem(Icons.Rounded.Thermostat, "Temp", "${log.waterTemperature}°C")
                TechItem(Icons.Rounded.Waves, "Turbulence", log.turbulence.name.lowercase().capitalize())
            }
        }
    }
}

@Composable
fun TechItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.width(150.dp)) {
        Box(
            modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
    }
}

// --- D. RECIPE STEPS ---
@Composable
fun RecipeSteps(log: CoffeeLog) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha=0.3f)),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            // Bloom Duration Header (using existing field)
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                 Icon(Icons.Rounded.Timer, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                 Spacer(Modifier.width(8.dp))
                 Text("Bloom Duration: ${log.bloomTime}s", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha=0.3f))
            Spacer(Modifier.height(12.dp))

            // Steps List
            if (log.recipeSteps.isNotEmpty()) {
                log.recipeSteps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Badge
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = if(index==0) 1f else 0.7f), RoundedCornerShape(50)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if(step.phase.startsWith("Bloom")) "B" else "${index}", 
                                color = MaterialTheme.colorScheme.onPrimary, 
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Spacer(Modifier.width(12.dp))
                        
                        // Phase & Time
                        Column(modifier = Modifier.weight(1f)) {
                            Text(step.phase, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            if (step.time.isNotBlank()) {
                                Text(step.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                            }
                        }
                        
                        // Water & Total
                        Column(horizontalAlignment = Alignment.End) {
                            Text("+${step.waterAdded.toInt()}g", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text("${step.totalWeight.toInt()}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    if (index < log.recipeSteps.lastIndex) {
                        Spacer(Modifier.height(12.dp))
                    }
                }
            } else {
                Text("No detailed steps recorded.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun StepRow(number: Int, title: String, detail: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(24.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
            contentAlignment = Alignment.Center
        ) {
            Text(number.toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(Modifier.width(12.dp))
        Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text(detail, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )
}

fun formatDateFull(timestamp: Long): String {
    val date = Date(timestamp)
    val pattern = "EEEE d MMM, HH:mm a"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return simpleDateFormat.format(date).capitalize()
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}

fun String.capitalize() = replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

