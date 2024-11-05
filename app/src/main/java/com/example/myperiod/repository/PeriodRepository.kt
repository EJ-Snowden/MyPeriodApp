package com.example.myperiod.repository

import androidx.lifecycle.LiveData
import com.example.myperiod.data.PeriodDao
import com.example.myperiod.data.PeriodEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class PeriodRepository @Inject constructor(private val periodDao: PeriodDao) {

    fun getAllPeriods(): LiveData<List<PeriodEntity>> = periodDao.getAllPeriods()

    suspend fun insertPeriods(periods: List<PeriodEntity>) {
        periodDao.insertAll(periods)
    }

    suspend fun updatePeriods(periods: List<PeriodEntity>) {
        periodDao.updateAll(periods)
    }

    suspend fun insertOrUpdatePeriods(periods: List<PeriodEntity>) {
        periodDao.insertAll(periods)
    }

    // In PeriodRepository
    suspend fun getCurrentPeriods(): List<PeriodEntity> {
        return periodDao.getAllPeriodsList() // Assuming a synchronous list retrieval method
    }
}