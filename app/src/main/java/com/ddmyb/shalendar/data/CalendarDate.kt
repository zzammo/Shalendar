package com.ddmyb.shalendar.data

data class CalendarDate(
    val year: Int,
    val month: Int,
    val date: Int,
    var scheduleList: List<Schedule>
)
