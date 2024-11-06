package com.example.myperiod.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.myperiod.data.PeriodEntity
import com.example.myperiod.repository.PeriodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class PeriodViewModel @Inject constructor(
    private val repository: PeriodRepository
) : ViewModel() {

    val allPeriods: LiveData<List<PeriodEntity>> = repository.getAllPeriods()
    var previousDay : LocalDate? = null
    var firstDay : LocalDate? = null
    var currentDay : LocalDate? = null
    var lastDay : LocalDate? = null
    var nextDay : LocalDate? = null

    fun initializePeriodsIfEmpty(startDate: LocalDate, periodDuration: Int, cycleLength: Int) {
        viewModelScope.launch {
            if (repository.getAllPeriods().value.isNullOrEmpty()) {
                val initialPeriods = mutableListOf<PeriodEntity>()
                for (i in 0 until periodDuration) {
                    initialPeriods.add(
                        PeriodEntity(
                            date = startDate.plusDays(i.toLong()),
                            isPeriodDay = true,
                            flowLevel = 1 + (i % 3) // Example flow level cycle
                        )
                    )
                }

                // Add expected periods for the next 11 months
                for (month in 1..11) {
                    val nextPeriodStart = startDate.plusDays((cycleLength * month).toLong())
                    for (i in 0 until periodDuration) {
                        initialPeriods.add(
                            PeriodEntity(
                                date = nextPeriodStart.plusDays(i.toLong()),
                                isPeriodDay = false,
                                flowLevel = 4 // Expected flow level
                            )
                        )
                    }
                }

                repository.insertPeriods(initialPeriods)
            }
        }
    }

    fun markPeriodDay(date: LocalDate, flowLevel: Int, periodDuration: Int, cycleLength: Int) {
        viewModelScope.launch {
            // Retrieve current periods from the database
            val currentPeriods = repository.getCurrentPeriods()

            // Create a mutable list excluding all existing expected days (level 4)
            val updatedPeriods = currentPeriods.filterNot { it.flowLevel == 4 }.toMutableList()

            // Check if the marked day is already part of an ongoing period
            val isPartOfOngoingPeriod = currentPeriods.any { it.date == date && it.flowLevel in 1..3 }

            // Check if the previous day is a confirmed period day (levels 1, 2, or 3)
            currentDay = date

            if (firstDay == null || firstDay!!.isAfter(currentDay)){
                firstDay = date
            }
            if (lastDay == null || lastDay!!.isBefore(currentDay)){
                lastDay = date
            }
            val isPreviousDayMarked = currentPeriods.any { firstDay == previousDay && it.flowLevel in 1..3 }
            previousDay = firstDay!!.minusDays(1)
            val isNextDayMarked = currentPeriods.any { lastDay == nextDay && it.flowLevel in 1..3 }
            nextDay = lastDay!!.plusDays(1)

            if (!isPartOfOngoingPeriod) {
                updatedPeriods.add(
                    PeriodEntity(
                        date = date,
                        isPeriodDay = true,
                        flowLevel = flowLevel
                    )
                )
            }

            // If marking a day backward (before an existing marked day), remove the last expected day
            if (isPreviousDayMarked) {
                // Find the last expected day within the current cycle
                val currentCycleEnd = date.plusDays(periodDuration - 1L)
                val lastExpectedDayInCycle = currentPeriods
                    .filter { it.flowLevel == 4 && it.date > date && it.date <= currentCycleEnd.plusDays(2) }
                    .maxByOrNull { it.date }

                // Remove the last expected day in the current cycle by setting it to level 0 (not expected)
                if (lastExpectedDayInCycle != null) {
                    updatedPeriods.removeAll { it.date == lastExpectedDayInCycle.date }
                    updatedPeriods.add(
                        PeriodEntity(
                            date = lastExpectedDayInCycle.date,
                            isPeriodDay = false,
                            flowLevel = 0 // Mark as not expected
                        )
                    )
                }
            }

            // Calculate expected days for the current cycle only if the previous day is not marked
            if (!isPreviousDayMarked && !isNextDayMarked) {
                for (i in 1 until periodDuration) {
                    val futureDate = date.plusDays(i.toLong())
                    val existingFuturePeriod = currentPeriods.find { it.date == futureDate }

                    // Only mark as expected if it is not already marked as a confirmed period (levels 1, 2, 3)
                    if (existingFuturePeriod == null || existingFuturePeriod.flowLevel !in 1..3) {
                        updatedPeriods.add(
                            PeriodEntity(
                                date = futureDate,
                                isPeriodDay = false,
                                flowLevel = 4 // Mark as an expected day
                            )
                        )
                    }
                }
            }

            // Calculate and add expected days for future cycles based on the cycle length
            if (!isPreviousDayMarked && !isNextDayMarked) {
                for (cycle in 1..11) {
                    val nextCycleStart = date.plusDays((cycleLength * cycle).toLong())

                    for (i in 0 until periodDuration) {
                        val cycleDate = nextCycleStart.plusDays(i.toLong())
                        val existingCyclePeriod = currentPeriods.find { it.date == cycleDate }

                        // Add only if not already marked with levels 1, 2, or 3
                        if (existingCyclePeriod == null || existingCyclePeriod.flowLevel !in 1..3) {
                            updatedPeriods.add(
                                PeriodEntity(
                                    date = cycleDate,
                                    isPeriodDay = false,
                                    flowLevel = 4 // Expected for future cycles
                                )
                            )
                        }
                    }
                }
            }

            if (isPreviousDayMarked) {
                // Find the last expected day within the current cycle
                for (cycle in 1..11) {
                    val nextCycleStart = date.plusDays((cycleLength * cycle).toLong())

                    val lastExpectedDayInCycle = nextCycleStart.plusDays(periodDuration.toLong())
                    updatedPeriods.removeAll { it.date == lastExpectedDayInCycle}
                    updatedPeriods.add(
                        PeriodEntity(
                            date = lastExpectedDayInCycle,
                            isPeriodDay = false,
                            flowLevel = 0 // Mark as not expected
                        )
                    )
                    updatedPeriods.add(
                        PeriodEntity(
                            date = nextCycleStart.minusDays(1),
                            isPeriodDay = false,
                            flowLevel = 4 // Mark as expected
                        )
                    )
                }
            }
            // Insert or update in the database
            repository.insertOrUpdatePeriods(updatedPeriods)
        }
    }
}