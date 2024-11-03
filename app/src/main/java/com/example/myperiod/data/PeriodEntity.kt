package com.example.myperiod.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "periods")
data class PeriodEntity(
    @PrimaryKey val date: LocalDate,
    val isPeriodDay: Boolean,
    val flowLevel: Int = 0, // 0 means no period
    val periodDayNumber: Int? = null // Indicates which day of the period it is (e.g., 1st day, 2nd day)
)
