package com.ddmyb.shalendar.view.schedules.utils

import java.io.Serializable
import java.time.LocalDateTime

data class StartDateTimeDto(
    val scheduleId: String?,
    val dateTime: LocalDateTime
):Serializable {
    override fun toString(): String {
        return "scheduleId: " + scheduleId.isNullOrEmpty().toString() + "dateTime: " + dateTime.toString()
    }
}