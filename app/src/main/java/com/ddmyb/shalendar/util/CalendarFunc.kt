package com.ddmyb.shalendar.util

import java.util.Calendar

object CalendarFunc {

    fun dayOfWeekOfDate(year: Int, month: Int, date: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month, date)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Monday"
            Calendar.TUESDAY -> "Tuesday"
            Calendar.WEDNESDAY -> "Wednesday"
            Calendar.THURSDAY -> "Thursday"
            Calendar.FRIDAY -> "Friday"
            Calendar.SATURDAY -> "Saturday"
            Calendar.SUNDAY -> "Sunday"
            else -> "Unknown"
        }
    }
}