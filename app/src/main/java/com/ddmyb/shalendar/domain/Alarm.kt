package com.ddmyb.shalendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
@RequiresApi(Build.VERSION_CODES.O)
data class Alarm(
    var name:String = "",
    var memo:String = "",
    var time:LocalDateTime = LocalDateTime.now(),
    var color:Int = 0
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
