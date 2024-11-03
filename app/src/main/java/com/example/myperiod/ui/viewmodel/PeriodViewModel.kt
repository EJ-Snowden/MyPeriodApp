package com.example.myperiod.ui.viewmodel

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
    private val repository: PeriodRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // LiveData for observing period data
    val allPeriods: LiveData<List<PeriodEntity>> = repository.getAllPeriods().asLiveData()

    // Insert a new period record
    fun addPeriod(period: PeriodEntity) {
        viewModelScope.launch {
            repository.insertPeriod(period)
        }
    }

    // Update a period record (optional)
    fun updatePeriod(period: PeriodEntity) {
        viewModelScope.launch {
            repository.updatePeriod(period)
        }
    }

    // Predict next period date
    fun predictNextPeriodDate(lastPeriodDate: LocalDate, averageCycleLength: Int): LocalDate {
        return lastPeriodDate.plusDays(averageCycleLength.toLong())
    }

    fun saveInitialSetup(cycleLength: Int, lastPeriodDate: LocalDate, periodDuration: Int) {
        viewModelScope.launch {
            // Create a new PeriodEntity with the provided information
            val periodEntity = PeriodEntity(
                date = lastPeriodDate,
                isPeriodDay = true, // Assuming initial period day
                flowLevel = 0, // Default or placeholder value
                periodDayNumber = 1 // The first day of the period
            )
            repository.insertPeriod(periodEntity)

            // Optionally save additional setup info as needed
            // For example, save cycle length and duration as shared preferences if needed
        }
    }
}
