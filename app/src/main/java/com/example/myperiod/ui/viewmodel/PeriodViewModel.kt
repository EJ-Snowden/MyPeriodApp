package com.example.myperiod.ui.viewmodel

import androidx.lifecycle.LiveData
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

    // LiveData to observe all period records
    val allPeriods: LiveData<List<PeriodEntity>> = repository.getAllPeriods().asLiveData()

    // Function to calculate average cycle length
    fun getAverageCycleLength(): LiveData<Int> {
        return repository.getAllPeriods().map { periods ->
            if (periods.size < 2) return@map 28 // Default average cycle length if not enough data
            val cycleLengths = periods.zipWithNext { a, b ->
                java.time.temporal.ChronoUnit.DAYS.between(a.date, b.date).toInt()
            }
            cycleLengths.average().toInt()
        }.asLiveData()
    }

    // Function to predict the next period date
    fun predictNextPeriodDate(lastPeriodDate: LocalDate, averageCycleLength: Int): LocalDate {
        return lastPeriodDate.plusDays(averageCycleLength.toLong())
    }

    // Function to check if today is a period day
    fun isTodayPeriodDay(): LiveData<Boolean> {
        return repository.getAllPeriods().map { periods ->
            val today = LocalDate.now()
            periods.any { it.date == today && it.isPeriodDay }
        }.asLiveData()
    }

    // Insert a new period record
    fun addPeriod(period: PeriodEntity) {
        viewModelScope.launch {
            repository.insertPeriod(period)
        }
    }

    // Update an existing period record
    fun updatePeriod(period: PeriodEntity) {
        viewModelScope.launch {
            repository.updatePeriod(period)
        }
    }
}