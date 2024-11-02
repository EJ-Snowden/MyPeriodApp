package com.example.myperiod.repository

import com.example.myperiod.data.PeriodDao
import com.example.myperiod.data.PeriodEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class PeriodRepository @Inject constructor(private val periodDao: PeriodDao) {
    fun getAllPeriods(): Flow<List<PeriodEntity>> = periodDao.getAllPeriods()

    suspend fun insertPeriod(period: PeriodEntity) {
        periodDao.insertPeriod(period)
    }

    suspend fun updatePeriod(period: PeriodEntity) {
        periodDao.updatePeriod(period)
    }

    suspend fun getPeriodByDate(date: LocalDate): PeriodEntity? {
        return periodDao.getPeriodByDate(date)
    }

    fun getAverageCycleLength(): Flow<Int> {
        return periodDao.getAllPeriods().map { periods ->
            if (periods.size < 2) return@map 0
            val cycleLengths = periods.zipWithNext { a, b ->
                java.time.temporal.ChronoUnit.DAYS.between(a.date, b.date).toInt()
            }
            cycleLengths.average().toInt()
        }
    }

    suspend fun predictNextPeriodDate(lastPeriodDate: LocalDate, averageCycleLength: Int): LocalDate {
        return lastPeriodDate.plusDays(averageCycleLength.toLong())
    }

    suspend fun isTodayPeriodDay(): Boolean {
        val today = LocalDate.now()
        val periodToday = periodDao.getPeriodByDate(today)
        return periodToday?.isPeriodDay ?: false
    }
}