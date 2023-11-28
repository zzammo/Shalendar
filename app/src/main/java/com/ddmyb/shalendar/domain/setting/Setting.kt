package com.ddmyb.shalendar.domain.setting

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto

@Entity
data class Setting(
    var vibration: Boolean = false,
    var alarm: Boolean = false,
    var calendars: String = ""
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
