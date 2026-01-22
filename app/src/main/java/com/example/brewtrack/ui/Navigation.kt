package com.example.brewtrack.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.brewtrack.ui.add_log.AddLogScreen
import com.example.brewtrack.ui.brews.BrewsScreen
import com.example.brewtrack.ui.calculator.RatioCalculatorScreen

object AppDestinations {
    const val CALCULATOR_ROUTE = "calculator"
    const val BREWS_ROUTE = "brews"
    const val ADD_LOG_ROUTE = "add_log"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestinations.CALCULATOR_ROUTE
    ) {
        composable(AppDestinations.CALCULATOR_ROUTE) {
            RatioCalculatorScreen(
                onNavigateToBrews = { navController.navigate(AppDestinations.BREWS_ROUTE) }
            )
        }
        composable(AppDestinations.BREWS_ROUTE) {
            BrewsScreen(
                onNavigateToCalculator = { navController.navigate(AppDestinations.CALCULATOR_ROUTE) },
                onNavigateToAddLog = { navController.navigate(AppDestinations.ADD_LOG_ROUTE) }
            )
        }
        composable(AppDestinations.ADD_LOG_ROUTE) {
            AddLogScreen(
                onLogSaved = { navController.popBackStack() }
            )
        }
    }
}
