package com.ddmyb.shalendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
data class ScheduleDto (
    var scheduleId: String = "",
    var groupId: String = "", // 개인 일정일 경우 "", 그룹 일정일 경우 그룹 id
    var userId: String = "",
    var color: Int = R.color.cat_0, // .

    // 일정 시작 시간
    var startMills: Long = 0L,
    var endMills: Long = 0L,

    // 소요 시간
    var meansType: String = "NULL",
    var costText: String = "",
    var costValue: Int = -1,

    // 출발 위치 + 도착 위치
    var srcLat: Double = -1.0,
    var srcLon: Double = -1.0,
    var dstLat: Double = -1.0,
    var dstLon: Double = -1.0,
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
        this.meansType = schedule.meansType.toString()
        this.costText = schedule.cost.getText()
        this.costValue = schedule.cost.getValue()
        if (schedule.srcPosition != null){
            this.srcLat = schedule.srcPosition!!.latitude
            this.srcLon = schedule.srcPosition!!.longitude
        }
        if (schedule.dstPosition != null){
            this.dstLat = schedule.dstPosition!!.latitude
            this.dstLon = schedule.dstPosition!!.longitude
        }
        this.srcAddress = schedule.srcAddress
        this.dstAddress = schedule.dstAddress
        this.dptMills = schedule.dptLocalDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        this.title = schedule.title
        this.memo = schedule.memo
    }
}