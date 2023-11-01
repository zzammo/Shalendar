package com.ddmyb.shalendar.util

import java.io.Serializable

data class NewScheduleDto(
    val scheduleId: String = "",
    val mills: Long = 0L
):Serializable {
    override fun toString(): String {
        return "scheduleId: " + scheduleId + "dateTime: " + mills.toString()
    }
}