package com.ddmyb.shalendar.domain

import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto

interface DBRepository {
    fun getCurrentUserUid(): String?
    suspend fun readUserSchedule(uid: String): List<ScheduleDto>
    suspend fun readGroupSchedule(groupId: String): List<ScheduleDto>
}