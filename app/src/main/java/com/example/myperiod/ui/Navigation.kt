package com.example.myperiod.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myperiod.data.PeriodEntity

@Composable
fun MyPeriodApp(startDestination: String) {
    val navController = rememberNavController()
    NavigationHost(navController = navController, startDestination = startDestination)
}

@Composable
fun NavigationHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("setup") {
            SetupScreen { periodLength, lastPeriodDate ->
                // Mark setup as completed and navigate to CalendarScreen
                // Add your logic to save completion state here
                navController.navigate("calendar")
            }
        }
        composable("calendar") {
            val periodData = listOf<PeriodEntity>() // Replace with actual data source
            CalendarScreen(
                periodData = periodData,
                onDayClick = { date ->
                    // Define the action when a day is clicked, e.g., show details or highlight
                },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

