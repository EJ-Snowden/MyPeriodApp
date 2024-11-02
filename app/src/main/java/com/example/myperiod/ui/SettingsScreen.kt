package com.example.myperiod.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(onPeriodLengthChange: (Int) -> Unit) {
    var newPeriodLength by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = newPeriodLength,
            onValueChange = { newPeriodLength = it },
            label = { Text("New Period Length (days)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val length = newPeriodLength.text.toIntOrNull()
                if (length != null) {
                    onPeriodLengthChange(length)
                } else {
                    // Handle invalid input if needed
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Update")
        }
    }
}

@Composable
fun SettingsScreen(onNavigateBack: () -> Unit) {
    // Your existing UI code
    Button(onClick = onNavigateBack) {
        Text("Back to Calendar")
    }
}
