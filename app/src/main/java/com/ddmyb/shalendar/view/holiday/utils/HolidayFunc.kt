package com.ddmyb.shalendar.view.holiday.utils

import java.util.Calendar

object HolidayFunc {
    fun YMDToMills(ymd: String): Long {
        val int_ymd = ymd.toInt()
        val year = int_ymd / 10000
        val month = int_ymd % 10000 / 100
        val date = int_ymd % 100
        val cal = Calendar.getInstance()
        cal[year, month - 1, date, 0, 0] = 0
        cal[Calendar.MILLISECOND] = 0
        return cal.timeInMillis
    }
}