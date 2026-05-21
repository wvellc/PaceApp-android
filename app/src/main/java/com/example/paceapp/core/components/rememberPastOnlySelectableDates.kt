package com.example.paceapp.core.components

import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.util.Calendar

@Composable
fun rememberPastOnlySelectableDates(): SelectableDates {
    return remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Allows today and any day in the past
                return utcTimeMillis <= System.currentTimeMillis()
            }

            override fun isSelectableYear(year: Int): Boolean {
                // Prevents swiping to future years entirely
                return year <= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    }
}