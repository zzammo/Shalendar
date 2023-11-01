package com.ddmyb.shalendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
data class Schedule (
    var scheduleId: String = "",
    var groupId: String = "", // 개인 일정일 경우 "", 그룹 일정일 경우 그룹 id
    var userId: String = "",
    var color: Int = R.color.cat_0, // .

    // 일정 시작 시간
    var startLocalDatetime: LocalDateTime = LocalDateTime.now(),
    var endLocalDatetime: LocalDateTime = LocalDateTime.now(),

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject = TextValueObject(text = "text",value = 10),

    // 출발 위치 + 도착 위치
    var srcPosition: LatLng? = null,
    var dstPosition: LatLng? = null,
    var srcAddress: String = "srcAddress",
    var dstAddress: String = "dstAddress",

    // 출발 예정 시각
    var dptLocalDateTime: LocalDateTime = LocalDateTime.now(),

    // 제목
    var title: String = "title",

    // 메모
    var memo: String = "memo"
){
    constructor(scheduleDto: ScheduleDto) : this(){
        this.scheduleId = scheduleDto.scheduleId
        this.groupId = scheduleDto.groupId
        this.userId = scheduleDto.userId
        this.color = scheduleDto.color
        this.startLocalDatetime = Instant.ofEpochMilli(scheduleDto.startMills).atZone(ZoneId.systemDefault()).toLocalDateTime()
        this.endLocalDatetime = Instant.ofEpochMilli(scheduleDto.endMills).atZone(ZoneId.systemDefault()).toLocalDateTime()
        this.meansType = scheduleDto.meansType
        this.cost = scheduleDto.cost
        if (scheduleDto.srcLat > 0.0){
            this.srcPosition = LatLng(scheduleDto.srcLat, scheduleDto.srcLon)
        }
        if (scheduleDto.dstLat > 0.0){
            this.dstPosition = LatLng(scheduleDto.dstLat, scheduleDto.dstLon)
        }
        this.srcAddress = scheduleDto.srcAddress
        this.dstAddress = scheduleDto.dstAddress
        this.dptLocalDateTime = Instant.ofEpochMilli(scheduleDto.dptMills).atZone(ZoneId.systemDefault()).toLocalDateTime()
        this.title = scheduleDto.title
        this.memo = scheduleDto.memo
    }
}