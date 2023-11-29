package com.ddmyb.shalendar.util

import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto

data class CalendarProvide(
    val calendar: String,
    val title: String?,
    val location: String?,
    val start: Long,
    val end: Long,
    val allDay: Int,
    val description: String?,
    val color: Int
) {
    fun toScheduleDto(): ScheduleDto {
        return if (allDay == 1) {
            ScheduleDto(
                title = title?:"",
                startMills = 0L,
                endMills = 0L,
                memo = description?:""
            )
        } else {
            ScheduleDto(
                title = title?:"",
                startMills = start,
                endMills = end,
                memo = description?:""
            )
        }
    }
}
