package net.paceapp.core.components

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


@Composable
fun rememberFutureOnlySelectableDates(): SelectableDates {
    return remember {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Allows today and any day in the future
                // We subtract 24 hours to ensure the entirety of "today" is selectable
                // regardless of the exact current millisecond.
                val yesterdayMillis = System.currentTimeMillis() - (24 * 60 * 60 * 1000)
                return utcTimeMillis >= yesterdayMillis
            }

            override fun isSelectableYear(year: Int): Boolean {
                // Prevents swiping to past years entirely
                return year >= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    }
}