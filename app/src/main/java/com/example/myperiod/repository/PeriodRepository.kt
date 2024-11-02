package com.example.myperiod.repository

import com.example.myperiod.data.PeriodDao
import com.example.myperiod.data.PeriodEntity
import kotlinx.coroutines.flow.Flow
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
}