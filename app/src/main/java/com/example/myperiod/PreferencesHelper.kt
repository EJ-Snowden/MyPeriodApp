package com.example.myperiod

import android.content.Context
import android.content.SharedPreferences

object PreferencesHelper {
    private const val PREFS_NAME = "my_period_prefs"
    private const val KEY_SETUP_COMPLETED = "setup_completed"
    private const val KEY_PERIOD_DURATION = "period_duration"

    fun isSetupCompleted(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(KEY_SETUP_COMPLETED, false)
    }

    fun setSetupCompleted(context: Context, completed: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putBoolean(KEY_SETUP_COMPLETED, completed)
            apply()
        }
    }

    // New methods to store and retrieve period duration
    fun getPeriodLength(context: Context): Int {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(KEY_PERIOD_DURATION, 28)
    }

    fun setPeriodLength(context: Context, duration: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt(KEY_PERIOD_DURATION, duration)
            apply()
        }
    }
}
