package com.example.brewtrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.brewtrack.ui.calculator.RatioCalculatorScreen
import com.example.brewtrack.ui.theme.BrewTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewTrackTheme {
                RatioCalculatorScreen()
            }
        }
    }
}
