package com.example.myperiod.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myperiod.utils.DateTypeConverter

@Database(entities = [PeriodEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao
}