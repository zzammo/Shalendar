package com.ddmyb.shalendar.domain.setting

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto

@Entity
data class Setting(
    var vibration: Boolean = true,
    var alarm: Boolean = true,
    var lunar: Boolean = true,
    var calendars: String = ""
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}
