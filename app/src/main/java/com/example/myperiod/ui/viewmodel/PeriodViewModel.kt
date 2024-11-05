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
            val updatedPeriods = mutableListOf<PeriodEntity>()

            // Add the marked day as day 1
            updatedPeriods.add(
                PeriodEntity(
                    date = date,
                    isPeriodDay = true,
                    flowLevel = flowLevel
                )
            )

            // Update the days after as expected periods
            for (i in 1 until periodDuration) {
                updatedPeriods.add(
                    PeriodEntity(
                        date = date.plusDays(i.toLong()),
                        isPeriodDay = false,
                        flowLevel = 4 // Expected flow
                    )
                )
            }

            // Update future periods for 11 months
            for (month in 1..11) {
                val nextPeriodStart = date.plusDays((cycleLength * month).toLong())
                for (i in 0 until periodDuration) {
                    updatedPeriods.add(
                        PeriodEntity(
                            date = nextPeriodStart.plusDays(i.toLong()),
                            isPeriodDay = false,
                            flowLevel = 4 // Expected flow
                        )
                    )
                }
            }

            repository.updatePeriods(updatedPeriods)
        }
    }
}