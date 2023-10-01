package com.ddmyb.shalendar.view.month.data

data class MonthPageData(
    val year: Int,
    val month: Int,
    val calendarDateList: MutableList<MonthCalendarDateData>
)
