package com.example.brewtrack.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.brewtrack.ui.add_log.AddLogScreen
import com.example.brewtrack.ui.brew_detail.BrewDetailScreen
import com.example.brewtrack.ui.brews.BrewsScreen
import com.example.brewtrack.ui.calculator.RatioCalculatorScreen

object AppDestinations {
    const val CALCULATOR_ROUTE = "calculator"
    const val BREWS_ROUTE = "brews"
    const val ADD_LOG_ROUTE = "add_log"
    const val BREW_DETAIL_ROUTE = "brew_detail"
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
                onNavigateToAddLog = { navController.navigate(AppDestinations.ADD_LOG_ROUTE) },
                onNavigateToDetail = { logId ->
                    navController.navigate("${AppDestinations.BREW_DETAIL_ROUTE}/$logId")
                }
            )
        }
        composable(
            route = "${AppDestinations.ADD_LOG_ROUTE}?logId={logId}",
            arguments = listOf(
                navArgument("logId") {
                     type = NavType.LongType
                     defaultValue = -1L
                }
            )
        ) {
            AddLogScreen(
                onLogSaved = { navController.popBackStack() }
            )
        }
        composable(
            route = "${AppDestinations.BREW_DETAIL_ROUTE}/{logId}",
            arguments = listOf(navArgument("logId") { type = NavType.LongType })
        ) {
            BrewDetailScreen(
                onNavigateBack = { navController.popBackStack() },
                onEditLog = { logId ->
                    navController.navigate("${AppDestinations.ADD_LOG_ROUTE}?logId=$logId")
                }
            )
        }
    }
}
