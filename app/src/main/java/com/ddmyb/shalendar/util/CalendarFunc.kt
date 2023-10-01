package com.ddmyb.shalendar.util

import java.util.Calendar

object CalendarFunc {

    fun dayOfWeekOfDate(timeInMills: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeInMills
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

    fun extractHM(timeInMills: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeInMills
        val hourInt = cal.get(Calendar.HOUR)
        val minuteInt = cal.get(Calendar.MINUTE)

        val hour = when(hourInt) {
            in 0 .. 9 -> "0${hourInt}"
            else -> "$hourInt"
        }
        val minute = when(minuteInt) {
            in 0 .. 9 -> "0${minuteInt}"
            else -> "$minuteInt"
        }

        return "$hour:$minute"
    }
}