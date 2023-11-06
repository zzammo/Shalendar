package com.ddmyb.shalendar.domain.Alarms

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto

@Entity
@RequiresApi(Build.VERSION_CODES.O)
data class Alarm(
    var name:String = "",
    var memo:String = "",
    var mills:Long = 0L,
    var color:Int = 0
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
    constructor(scheduleDto: ScheduleDto): this(){
        this.name = scheduleDto.title
        this.memo = scheduleDto.memo
        this.mills = scheduleDto.dptMills
        this.color = scheduleDto.color
    }
}
