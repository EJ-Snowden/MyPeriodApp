package com.example.myperiod.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myperiod.R
import com.example.myperiod.data.PeriodEntity
import com.example.myperiod.ui.viewmodel.PeriodViewModel
import java.time.LocalDate

@Composable
fun CalendarScreen(
    periodData: List<PeriodEntity>,
    onDayClick: (LocalDate) -> Unit,
    onMarkPeriod: (LocalDate, Int, Int) -> Unit,
    periodDuration: Int,
    averageCycleLength: Int,
    periodViewModel: PeriodViewModel = hiltViewModel()
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var currentYear by remember { mutableIntStateOf(currentMonth.year) }
    val currentDate = LocalDate.now()
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedFlow by remember { mutableStateOf("None") }
    val observedPeriodData by periodViewModel.allPeriods.observeAsState(initial = emptyList())

    val updatedPeriodData = observedPeriodData.toMutableList()

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
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Calendar header
        CalendarHeader(
            currentMonth = currentMonth,
            currentYear = currentYear,
            onMonthChange = { change -> currentMonth = currentMonth.plusMonths(change) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Calendar grid display
        CalendarGrid(
            periodData = updatedPeriodData,
            currentMonth = currentMonth,
            currentDate = currentDate,
            onDayClick = { date ->
                selectedDate = date
                onDayClick(date)
            },
            boxSize = boxSize
        )

        Spacer(modifier = Modifier.weight(1f))

        // Divider for clear sectioning
        Divider(color = Color(0xFFD81B60), thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

        // Detailed section for the selected date
        DateDetails(
            currentDate = currentDate,
            selectedFlow = selectedFlow,
            periodViewModel = periodViewModel,
            selectedDate = selectedDate,
            periodDuration = periodDuration,
            averageCycleLength = averageCycleLength
        )
    }
}


@Composable
fun CalendarHeader(
    currentMonth: LocalDate,
    currentYear: Int,
    onMonthChange: (Long) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = { onMonthChange(-1) }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                contentDescription = "Previous month",
                tint = Color(0xFFD81B60)
            )
        }

        Text(
            text = "${currentMonth.month.name.capitalize()} $currentYear", // Show month and year
            style = MaterialTheme.typography.headlineMedium.copy(color = Color(0xFFD81B60))
        )

        IconButton(onClick = { onMonthChange(1) }) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                contentDescription = "Next month",
                tint = Color(0xFFD81B60)
            )
        }
    }
}

@Composable
fun CalendarGrid(
    periodData: List<PeriodEntity>,
    currentMonth: LocalDate,
    currentDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    boxSize: Dp // Accept boxSize as a parameter
) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.withDayOfMonth(1).dayOfWeek.value

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Show the day names
        DayNamesRow()

        // Adjust the grid to start from the correct weekday
        var dayCounter = 1
        for (week in 0 until 6) { // Allow for up to 6 weeks
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for (dayOfWeek in 1..7) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek) {
                        // Empty box for days before the first day of the month
                        Box(modifier = Modifier.size(boxSize))
                    } else if (dayCounter <= daysInMonth) {
                        // Box for a valid day in the month
                        DayBox(
                            date = currentMonth.withDayOfMonth(dayCounter),
                            periodData = periodData,
                            currentDate = currentDate,
                            onDayClick = onDayClick,
                            boxSize = boxSize // Pass the size for day boxes
                        )
                        dayCounter++ // Move to the next day
                    } else {
                        // Empty box for days after the end of the month
                        Box(modifier = Modifier.size(boxSize))
                    }
                }
            }
        }
    }
}

@Composable
fun DayNamesRow() {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
            Text(
                day,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DayBox(
    date: LocalDate,
    periodData: List<PeriodEntity>,
    currentDate: LocalDate,
    onDayClick: (LocalDate) -> Unit,
    boxSize: Dp
) {
    val periodEntity = periodData.find { it.date == date }
    val isCurrentDay = date == currentDate

    // Determine box color based on the day status and flow level
    val boxColor = when {
        isCurrentDay && periodEntity?.isPeriodDay == true -> {
            when (periodEntity.flowLevel) {
                1 -> Color(0xFFFF7696) // Light pink for flow level 1
                2 -> Color(0xFFF80C30) // Medium pink for flow level 2
                3 -> Color(0xFFA80004) // Dark red for flow level 3
                4 -> Color(0xFFFFCCD8) // Expected color
                else -> Color(0xFF69D0FF) // Blue for today if no period
            }
        }
        periodEntity != null -> {
            when (periodEntity.flowLevel) {
                1 -> Color(0xFFFF7696) // Light pink for flow level 1
                2 -> Color(0xFFF80C30) // Medium pink for flow level 2
                3 -> Color(0xFFA80004) // Dark red for flow level 3
                4 -> Color(0xFFFFCCD8) // Expected color
                else -> Color(0xFFF80C30) // Default for period days
            }
        }
        else -> Color(0xFFE0E0E0) // Default color
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(boxSize)
            .background(color = boxColor, shape = CircleShape)
            .clickable { onDayClick(date) }
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            color = if (isCurrentDay || periodEntity != null) Color.White else Color.Black,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun DateDetails(
    currentDate: LocalDate,
    selectedFlow: String,
    periodViewModel : PeriodViewModel,
    selectedDate : LocalDate,
    periodDuration : Int,
    averageCycleLength : Int
) {
    var currentFlow by remember { mutableStateOf(selectedFlow) }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = ("Chosen day: $currentDate"), style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.padding(vertical = 8.dp)) {
            FlowButton("Light", R.drawable.low, currentFlow == "Light") {
                currentFlow = "Light"
            }
            FlowButton("Medium", R.drawable.medium, currentFlow == "Medium") {
                currentFlow = "Medium"
            }
            FlowButton("Heavy", R.drawable.hard, currentFlow == "Heavy") {
                currentFlow = "Heavy"
            }
        }

        Button(
            onClick = {
                periodViewModel.markPeriodDay(selectedDate, 1, periodDuration, averageCycleLength)
            },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD81B60)),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Mark Period Today", color = Color.White)
        }
    }
}


@Composable
fun FlowButton(flowLevel: String, iconResId: Int, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = if (isSelected) Color.Gray else Color.White),
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