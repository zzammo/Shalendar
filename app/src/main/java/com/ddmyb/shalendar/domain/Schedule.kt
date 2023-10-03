package com.ddmyb.shalendar.domain

import android.location.Location
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class Schedule (
    private var scheduleId: String? = null,

    var startLocalDateTime: LocalDateTime? = null,

    // 일정 시작 정보
    var startHour: Int = 8,
    var startMinute: Int = 0,
    var startMonth: Int = 1,
    var startDay: Int = 1,
    var startYear: Int = 2023,
    var startWeek: Int = 0,

    // 일정이 끝나는 정보
    var endHour: Int = 9,
    var endMinute: Int = 0,
    var endMonth: Int = 1,
    var endDay: Int = 1,
    var endYear: Int = 2023,
    var endWeek: Int = 0,

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var costHour: Int = 0,
    var costMinute: Int = 0,
    var costSecond: Int = 0,

    // 출발 위치 + 도착 위치
    var srcLocation: Location? = null,
    var dstLocation: Location? = null,
    var srcPosition: LatLng? = null,
    var dstPosition: LatLng? = null,
    var srcAddress: String? = null,
    var dstAddress: String? = null,

    // 출발 예정 시각 : 년 월 일 시간 분
    var expectedStartYear: Int = 0,
    var expectedStartHour: Int = 0,
    var expectedStartMinute: Int = 0,
    var expectedStartMonth: Int = 0,
    var expectedStartDay: Int = 0,

    var alarm: String? = null,
    var memo: String? = null
)