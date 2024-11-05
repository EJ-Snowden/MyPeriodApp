package com.example.myperiod.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface PeriodDao {
    @Query("SELECT * FROM periods ORDER BY date ASC")
    fun getAllPeriods(): LiveData<List<PeriodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(periods: List<PeriodEntity>)

    @Update
    suspend fun updateAll(periods: List<PeriodEntity>)

    @Query("SELECT * FROM periods ORDER BY date ASC")
    suspend fun getAllPeriodsList(): List<PeriodEntity>
}
