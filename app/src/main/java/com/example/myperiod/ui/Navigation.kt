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
        onMarkPeriod = { date, flowLevel, duration -> },
        averageCycleLength = 28,
        periodDuration = 5
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
            val periodViewModel: PeriodViewModel = hiltViewModel(backStackEntry) // Ensure context is provided
            SetupScreen(periodViewModel) { periodLength, lastPeriodDate, duration ->
                navController.navigate("calendar")
            }
        }
        composable("calendar") { backStackEntry ->
            val periodViewModel: PeriodViewModel = hiltViewModel(backStackEntry)
            val periodData = periodViewModel.allPeriods.observeAsState(emptyList())

            CalendarScreen(
                periodData = periodData.value,
                onDayClick = { date -> },
                onMarkPeriod = { date, flowLevel, periodDuration ->
                    onMarkPeriod(date, flowLevel, periodDuration)
                },
                averageCycleLength = averageCycleLength,
                periodDuration = periodDuration
            )
        }
    }
}

