package com.ddmyb.shalendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
data class ScheduleDto (
    var scheduleId: String = "scheduleId",
    var groupId: Int = -1, // 개인 일정일 경우 -1, 그룹 일정일 경우 그룹 id
    var userId: String = "",
    var color: Int = R.color.cat_0, // .

    // 일정 시작 시간
    var startMills: Long = 0L,
    var endMills: Long = 0L,

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject = TextValueObject(text = "text",value = 10),

    // 출발 위치 + 도착 위치
    var srcPosition: LatLng = LatLng(12.34,30.56),
    var dstPosition: LatLng = LatLng(12.34,30.56),
    var srcAddress: String = "srcAddress",
    var dstAddress: String = "dstAddress",

    // 출발 예정 시각
    var dptMills: Long = 0L,

    // 제목
    var title: String = "title",

    // 메모
    var memo: String = "memo"

){
    constructor(schedule: Schedule) : this() {
        this.scheduleId = schedule.scheduleId
        this.groupId = schedule.groupId
        this.userId = schedule.userId
        this.color = schedule.color
        this.startMills = schedule.startLocalDatetime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        this.endMills = schedule.endLocalDatetime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        this.meansType = schedule.meansType
        this.cost = schedule.cost
        this.srcPosition = schedule.srcPosition
        this.dstPosition = schedule.dstPosition
        this.srcAddress = schedule.srcAddress
        this.dstAddress = schedule.dstAddress
        this.dptMills = schedule.dptLocalDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        this.title = schedule.title
        this.memo = schedule.memo
    }
}