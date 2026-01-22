package com.example.brewtrack.ui.brews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brewtrack.model.CoffeeLog
import com.example.brewtrack.ui.theme.BrewTrackTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("My Brews") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Handle FAB click */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Add new brew")
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(dummyBrews) {
                BrewCard(brew = it)
            }
        }
    }
}

@Composable
fun BrewCard(brew: CoffeeLog) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = formatDate(brew.date),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = brew.beanName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = brew.method,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, contentDescription = "Rating", tint = MaterialTheme.colorScheme.tertiary)
                    Text(text = "${brew.rating}/5", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

fun formatDate(date: Date): String {
    val pattern = "d MMMM, HH:mm"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return simpleDateFormat.format(date)
}

val dummyBrews = listOf(
    CoffeeLog(beanName = "Ethiopia Yirgacheffe", method = "V60", rating = 4, date = Date(), ratio = 16.0f, water = 250f, coffee = 15.6f),
    CoffeeLog(beanName = "Colombia Supremo", method = "Aeropress", rating = 5, date = Date(), ratio = 15.0f, water = 220f, coffee = 14.6f),
    CoffeeLog(beanName = "Kenya AA", method = "French Press", rating = 3, date = Date(), ratio = 17.5f, water = 350f, coffee = 20.0f)
)

@Preview(showBackground = true)
@Composable
fun BrewsScreenPreview() {
    BrewTrackTheme {
        BrewsScreen()
    }
}
