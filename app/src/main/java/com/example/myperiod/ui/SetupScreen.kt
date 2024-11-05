package com.example.myperiod.ui

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myperiod.PreferencesHelper
import com.example.myperiod.ui.viewmodel.PeriodViewModel
import java.time.LocalDate
import java.util.*

@Composable
fun SetupScreen(
    periodViewModel: PeriodViewModel, // Inject ViewModel here
    onSetupComplete: (Int, LocalDate, Int) -> Unit
) {
    var periodLength by remember { mutableStateOf("") }
    var lastPeriodDate by remember { mutableStateOf(LocalDate.now()) }
    var periodDuration by remember { mutableStateOf("") } // New state for period duration
    val backgroundColor = Color(0xFFFFC0CB) // Light pink color
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Setup Your Period Tracker",
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFFD81B60)),
            fontSize = 24.sp
        )

        OutlinedTextField(
            value = periodLength,
            onValueChange = { periodLength = it },
            label = { Text("Average Cycle Length (days)", color = Color(0xFFD81B60)) },
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
            singleLine = true,
            placeholder = { Text("e.g., 28") }
        )

        OutlinedTextField(
            value = periodDuration,
            onValueChange = { periodDuration = it },
            label = { Text("Period Duration (days)", color = Color(0xFFD81B60)) },
            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)),
            singleLine = true,
            placeholder = { Text("e.g., 5") }
        )

        DatePickerField(
            selectedDate = lastPeriodDate,
            onDateChange = { lastPeriodDate = it },
            label = "Select Last Period Date"
        )

        Button(
            onClick = {
                val cycleLength = periodLength.toIntOrNull()
                val duration = periodDuration.toIntOrNull()
                val length = periodLength.toIntOrNull()
                if (cycleLength != null && duration != null && length != null) {
                    PreferencesHelper.setSetupCompleted(context, true)
                    PreferencesHelper.setPeriodLength(context, length)
                    // Save to database using ViewModel
                    periodViewModel.initializePeriodsIfEmpty(lastPeriodDate, duration, cycleLength)
                    onSetupComplete(cycleLength, lastPeriodDate, duration)
                } else {
                    // Handle input validation error (optional)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60))
        ) {
            Text("Continue", color = Color.White)
        }
    }
}


@Composable
fun DatePickerField(
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    label: String
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        DatePickerDialog(
            LocalContext.current,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                onDateChange(LocalDate.of(year, month + 1, dayOfMonth))
                showDialog = false
            },
            selectedDate.year,
            selectedDate.monthValue - 1,
            selectedDate.dayOfMonth
        ).show()
    }

    OutlinedTextField(
        value = selectedDate.toString(),
        onValueChange = { /* No direct text input change */ },
        label = { Text(label, color = Color(0xFFD81B60)) },
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clickable { showDialog = true }
    )
}