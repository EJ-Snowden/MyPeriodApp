package com.example.myperiod.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import java.time.LocalDate

@Composable
fun SetupScreen(onSubmit: (Int, LocalDate) -> Unit) {
    var periodLength by remember { mutableStateOf(TextFieldValue("")) }
    var lastPeriodDate by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = periodLength,
            onValueChange = { periodLength = it },
            label = { Text("Period Length (days)") },
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = lastPeriodDate,
            onValueChange = { lastPeriodDate = it },
            label = { Text("Last Period Start Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val length = periodLength.text.toIntOrNull()
                val date = runCatching { LocalDate.parse(lastPeriodDate.text) }.getOrNull()

                if (length != null && date != null) {
                    onSubmit(length, date)
                } else {
                    // Handle invalid input if needed
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Submit")
        }
    }
}