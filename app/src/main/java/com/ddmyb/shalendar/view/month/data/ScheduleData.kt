package com.ddmyb.shalendar.view.month.data

import android.graphics.Color

data class ScheduleData(
    val name: String,
    val startTime: Long,
    val endTime: Long,
    val isWholeDay: Boolean = false,
    val color: Int = Color.BLUE
)
