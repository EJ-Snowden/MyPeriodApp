package com.example.myperiod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myperiod.data.PeriodEntity
import java.time.LocalDate

@Composable
fun CalendarScreen(
    periodData: List<PeriodEntity>,
    onDayClick: (LocalDate) -> Unit
) {
    // Placeholder for a simple calendar representation
    Column(modifier = Modifier.padding(16.dp)) {
        (1..30).forEach { day ->
            val date = LocalDate.now().withDayOfMonth(day)
            val isPeriodDay = periodData.any { it.date == date && it.isPeriodDay }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)
                    .background(if (isPeriodDay) Color.Red else Color.LightGray)
                    .clickable { onDayClick(date) }
            ) {
                BasicText(text = day.toString())
            }
        }
    }
}