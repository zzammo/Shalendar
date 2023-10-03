package com.ddmyb.shalendar.view.schedules.utils

import java.io.Serializable
import java.time.LocalDateTime

data class StartDateTimeDto(
    val scheduleId: String?,
    val dateTime: LocalDateTime
):Serializable
