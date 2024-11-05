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

            // Add the marked day as day 1 if it is not already a confirmed part of the period
            if (!isPartOfOngoingPeriod) {
                updatedPeriods.add(
                    PeriodEntity(
                        date = date,
                        isPeriodDay = true,
                        flowLevel = flowLevel
                    )
                )
            }

            // **Calculate expected days for the current cycle**
            // This is only done after the first day is placed and ensures the `periodDuration` is maintained
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

            // Calculate and add expected days for future cycles based on the cycle length
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

            // Insert or update in the database
            repository.insertOrUpdatePeriods(updatedPeriods)
        }
    }

}