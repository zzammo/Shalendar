package com.ddmyb.shalendar.view.schedules

import android.location.Location
import com.google.android.gms.maps.model.LatLng

class Schedule {
    private var scheduleId: String? = null

    // 일정 시작 정보
    var startHour = 8
    var startMinute = 0
    var startMonth = 1
    var startDay = 1
    var startYear = 2023
    var startWeek = 0

    // 일정이 끝나는 정보
    var endHour = 9
    var endMinute = 0
    var endMonth = 1
    var endDay = 1
    var endYear = 2023
    var endWeek = 0

    // 소요 시간
    var costHour = 0
    var costMinute = 0
    var costSecond = 0

    // 출발 위치 + 도착 위치
    var srcLocation: Location? = null
    var dstLocation: Location? = null
    var srcPosition: LatLng? = null
    var dstPosition: LatLng? = null
    var srcAddress: String? = null
    var dstAddress: String? = null

    // 출발 예정 시각 : 년 월 일 시간 분
    var expectedStartYear = 0
    var expectedStartHour = 0
    var expectedStartMinute = 0
    var expectedStartMonth = 0
    var expectedStartDay = 0

    var alarm: String? = null
    var memo: String? = null
}