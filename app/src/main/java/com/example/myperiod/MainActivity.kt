package com.example.myperiod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myperiod.ui.MyPeriodApp
import com.example.myperiod.ui.SetupScreen
import com.example.myperiod.ui.theme.MyPeriodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isSetupCompleted = PreferencesHelper.isSetupCompleted(this)

        setContent {
            MyPeriodTheme {
                MyPeriodApp(startDestination = if (isSetupCompleted) "calendar" else "setup")
            }
        }
    }
}