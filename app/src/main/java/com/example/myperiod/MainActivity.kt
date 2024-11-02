package com.example.myperiod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.myperiod.ui.SetupScreen
import com.example.myperiod.ui.theme.MyPeriodTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyPeriodTheme {
                SetupScreen { periodLength, lastPeriodDate ->
                    // Handle period length and last period date input, or navigate to the next screen
                    // Logic to store these values or proceed can be added here
                }
            }
        }
    }
}