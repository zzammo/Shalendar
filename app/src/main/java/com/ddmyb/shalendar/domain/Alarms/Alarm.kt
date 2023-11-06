package com.ddmyb.shalendar.domain.Alarms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import java.util.UUID

@Entity
@RequiresApi(Build.VERSION_CODES.O)
data class Alarm(
    var name:String = "",
    var memo:String = "",
    var mills:Long = 0L,
    var color:Int = 0,
    var alarmMills:Long = 0L
){
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    constructor(scheduleDto: ScheduleDto, alarmMills: Long): this(){
        this.name = scheduleDto.title
        this.memo = scheduleDto.memo
        this.mills = scheduleDto.startMills
        this.color = scheduleDto.color
        this.alarmMills = alarmMills
    }
}
