package com.example.myperiod.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myperiod.R
import com.example.myperiod.data.PeriodEntity
import java.time.LocalDate

@Composable
fun CalendarScreen(
    periodData: List<PeriodEntity>,
    onDayClick: (LocalDate) -> Unit,
    onMarkPeriod: (LocalDate, Int, Int) -> Unit,
    periodDuration: Int,
    averageCycleLength: Int
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val currentDate = LocalDate.now()
    var selectedDate by remember { mutableStateOf(currentDate) }
    var selectedFlow by remember { mutableStateOf("None") }

    // Screen width calculation for the calendar layout
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
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Calendar header
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_24), contentDescription = "Previous month", tint = Color(0xFFD81B60))
                }

                Text(text = currentMonth.month.name.capitalize(), style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFFD81B60)))

                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_arrow_forward_24), contentDescription = "Next month", tint = Color(0xFFD81B60))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar grid display
            val daysInMonth = currentMonth.lengthOfMonth()
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (week in 0..(daysInMonth / 7)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(spacingBetweenBoxes)) {
                        for (day in 1..7) {
                            val currentDay = week * 7 + day
                            if (currentDay <= daysInMonth) {
                                val date = currentMonth.withDayOfMonth(currentDay)
                                val isPeriodDay = periodData.any { it.date == date && it.isPeriodDay }
                                val isCurrentDay = date == currentDate
                                val isInPastPeriod = (date.isAfter(currentDate.minusDays(periodDuration.toLong() - 1)) && date.isBefore(currentDate.plusDays(1)))

                                // Determine box color based on the day status
                                val boxColor = when {
                                    isCurrentDay -> Color(0xFFFF69B4) // Pink for today
                                    isPeriodDay -> Color(0xFFD81B60) // Red for period days
                                    isInPastPeriod -> Color(0xFFFFB6C1) // Light pink for past period days
                                    else -> Color(0xFFE0E0E0) // Default color
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(boxSize)
                                        .background(color = boxColor, shape = CircleShape)
                                        .clickable {
                                            selectedDate = date
                                            selectedFlow = if (date.isBefore(currentDate)) {
                                                // Logic for past day flow level
                                                periodData.find { it.date == date }?.let {
                                                    when (it.flowLevel) {
                                                        1 -> "Light"
                                                        2 -> "Medium"
                                                        3 -> "Heavy"
                                                        4 -> "Disaster"
                                                        else -> "None"
                                                    }
                                                } ?: "None"
                                            } else {
                                                "None"
                                            }
                                        }
                                ) {
                                    Text(
                                        text = currentDay.toString(),
                                        color = if (isCurrentDay || isPeriodDay || isInPastPeriod) Color.White else Color.Black,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Divider for clear sectioning
        Divider(color = Color(0xFFD81B60), thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

        // Detailed section for the selected date
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = if (selectedDate.isBefore(currentDate) && periodData.any { it.date == selectedDate && it.isPeriodDay }) {
                    val periodDayNumber = periodData.find { it.date == selectedDate }?.periodDayNumber ?: 0
                    "Day $periodDayNumber of Period"
                } else if (selectedDate == currentDate) {
                    if (periodData.any { it.date == selectedDate && it.isPeriodDay }) {
                        "Currently: Day ${periodData.find { it.date == selectedDate }?.periodDayNumber} of Period"
                    } else {
                        // Calculate how many days until the next predicted period
                        val lastPeriodDate = periodData.filter { it.isPeriodDay }.maxByOrNull { it.date }?.date ?: currentDate
                        val nextPeriodDate = lastPeriodDate.plusDays(averageCycleLength.toLong())
                        if (nextPeriodDate.isAfter(currentDate)) {
                            "Period in ${nextPeriodDate.toEpochDay() - currentDate.toEpochDay()} days"
                        } else {
                            "Calculating next period..."
                        }
                    }
                } else {
                    // Display information for future selected date
                    "Selected date: $selectedDate"
                },
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
            )

            // If current date, show flow buttons; for past dates, show summary
            if (selectedDate == currentDate) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    FlowButton("Light", R.drawable.ic_light_flow)
                    FlowButton("Medium", R.drawable.ic_medium_flow)
                    FlowButton("Heavy", R.drawable.ic_heavy_flow)
                    FlowButton("Disaster", R.drawable.ic_disaster_flow)
                }
                // Button to mark today as a period day
                Button(
                    onClick = {
                        // Create a new PeriodEntity for today and mark future days
                        for (i in 0 until periodDuration) {
                            val futureDate = selectedDate.plusDays(i.toLong())
                            val periodEntity = PeriodEntity(
                                date = futureDate,
                                isPeriodDay = true,
                                flowLevel = 1, // Replace with logic for chosen flow level
                                periodDayNumber = i + 1 // Adjust the day number accordingly
                            )
                            onMarkPeriod(futureDate, 1, periodDuration) // Pass necessary parameters
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Mark Period from Today", color = Color.White)
                }
            } else if (selectedDate.isBefore(currentDate)) {
                Text(
                    text = "Flow Level: $selectedFlow",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color.Gray)
                )
                Image(
                    painter = painterResource(
                        id = when (selectedFlow) {
                            "Light" -> R.drawable.ic_light_flow
                            "Medium" -> R.drawable.ic_medium_flow
                            "Heavy" -> R.drawable.ic_heavy_flow
                            "Disaster" -> R.drawable.ic_disaster_flow
                            else -> R.drawable.ic_no_flow
                        }
                    ),
                    contentDescription = "Flow Level Icon",
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Composable
fun FlowButton(flowLevel: String, iconResId: Int) {
    Button(
        onClick = { /* Handle flow selection */ },
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier.size(60.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = "$flowLevel flow icon",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(60.dp)
            )
        }
    }
}

