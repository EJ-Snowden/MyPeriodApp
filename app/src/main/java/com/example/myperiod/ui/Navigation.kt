package com.example.myperiod.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myperiod.data.PeriodEntity
import com.example.myperiod.ui.viewmodel.PeriodViewModel
import java.time.LocalDate

@Composable
fun MyPeriodApp(startDestination: String) {
    val navController = rememberNavController()
    NavigationHost(
        navController = navController,
        startDestination = startDestination,
        onMarkPeriod = { date, flowLevel, duration ->
            // This could call a function in PeriodViewModel to handle marking the period.
            // You need to pass PeriodViewModel or ensure access to it here.
        },
        averageCycleLength = 28, // Default value or fetched from user preferences
        periodDuration = 5 // Default value or fetched from user preferences
    )
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: String,
    onMarkPeriod: (LocalDate, Int, Int) -> Unit,
    averageCycleLength: Int,
    periodDuration: Int
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("setup") { backStackEntry ->
            val periodViewModel: PeriodViewModel = hiltViewModel(backStackEntry)
            SetupScreen(periodViewModel) { periodLength, lastPeriodDate, duration ->
                // After setup, navigate to the calendar screen and pass necessary arguments if needed
                navController.navigate("calendar")
            }
        }
        composable("calendar") { backStackEntry ->
            val periodViewModel: PeriodViewModel = hiltViewModel(backStackEntry)
            val periodData = periodViewModel.allPeriods.observeAsState(emptyList()).value

            CalendarScreen(
                periodData = periodData,
                onDayClick = { date -> },
                onMarkPeriod = { date, flowLevel, duration ->
                    periodViewModel.markPeriodDay(date, flowLevel, periodDuration, averageCycleLength)
                    onMarkPeriod(date, flowLevel, duration) // Optional: Call external logic if needed
                },
                averageCycleLength = averageCycleLength,
                periodDuration = periodDuration
            )
        }
    }
}