package com.example.myperiod.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myperiod.R
import com.example.myperiod.data.PeriodEntity
import java.time.LocalDate

@Composable
fun CalendarScreen(
    periodData: List<PeriodEntity>,
    onDayClick: (LocalDate) -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val currentDate = LocalDate.now()

    // Screen width calculation to fit calendar boxes
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val horizontalPadding = 32.dp
    val spacingBetweenBoxes = 4.dp
    val totalSpacing = spacingBetweenBoxes * 6
    val boxSize = (screenWidth - horizontalPadding - totalSpacing) / 7

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFE4E1))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                    contentDescription = "Previous month",
                    tint = Color(0xFFD81B60)
                )
            }

            Text(
                text = currentMonth.month.name.capitalize(),
                style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFFD81B60)),
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                    contentDescription = "Next month",
                    tint = Color(0xFFD81B60)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val daysInMonth = currentMonth.lengthOfMonth()
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            for (week in 0..(daysInMonth / 7)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacingBetweenBoxes)
                ) {
                    for (day in 1..7) {
                        val currentDay = week * 7 + day
                        if (currentDay <= daysInMonth) {
                            val date = currentMonth.withDayOfMonth(currentDay)
                            val isPeriodDay = periodData.any { it.date == date && it.isPeriodDay }

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(boxSize)
                                    .clip(CircleShape) // Make the boxes more circular
                                    .background(
                                        color = when {
                                            date == currentDate -> Color(0xFFFF69B4)
                                            isPeriodDay -> Color(0xFFD81B60)
                                            else -> Color(0xFFE0E0E0)
                                        }
                                    )
                                    .clickable { onDayClick(date) }
                            ) {
                                Text(
                                    text = currentDay.toString(),
                                    color = if (date == currentDate || isPeriodDay) Color.White else Color.Black,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display the current day and period status section
        Text(
            text = "Today: ${currentDate.dayOfMonth} ${currentMonth.month.name.capitalize()}",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color(0xFFD81B60)),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = if (periodData.any { it.date == currentDate && it.isPeriodDay }) {
                "Period Day"
            } else {
                "Period in X days"
            },
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
        )

        Button(
            onClick = { /* Add logic for "Today I have..." */ },
            modifier = Modifier.padding(top = 16.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("Today I Have...", color = Color.White)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Go to Settings", color = Color.White)
        }
    }
}
