package com.example.myperiod.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods")
    fun getAllPeriods(): Flow<List<PeriodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeriod(period: PeriodEntity)

    @Update
    suspend fun updatePeriod(period: PeriodEntity)

    @Query("SELECT * FROM periods WHERE date = :date")
    suspend fun getPeriodByDate(date: LocalDate): PeriodEntity?
}