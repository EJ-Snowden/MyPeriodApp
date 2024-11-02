package com.example.myperiod.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "periods")
data class PeriodEntity(
    @PrimaryKey val date: LocalDate,
    val isPeriodDay: Boolean
)