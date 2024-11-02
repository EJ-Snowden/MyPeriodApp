package com.example.myperiod.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myperiod.utils.DateTypeConverter

@Database(entities = [PeriodEntity::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class PeriodDatabase : RoomDatabase() {
    abstract fun periodDao(): PeriodDao

    companion object {
        @Volatile
        private var INSTANCE: PeriodDatabase? = null

        fun getDatabase(context: Context): PeriodDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PeriodDatabase::class.java,
                    "period_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}