package com.example.myperiod.di

import android.content.Context
import androidx.room.Room
import com.example.myperiod.data.PeriodDao
import com.example.myperiod.data.PeriodDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): PeriodDatabase {
        return Room.databaseBuilder(
            context,
            PeriodDatabase::class.java,
            "period_database"
        ).build()
    }

    @Provides
    fun providePeriodDao(database: PeriodDatabase): PeriodDao {
        return database.periodDao()
    }
}
