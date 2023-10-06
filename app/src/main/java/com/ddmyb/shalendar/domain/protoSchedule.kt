package com.ddmyb.shalendar.domain

import android.location.Location
import android.os.Build
import android.provider.AlarmClock
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.distance.model.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class protoSchedule (
    var scheduleId: String? = "scheduleId",

    var startLocalDateTime: LocalDateTime? = LocalDateTime.now(),
    var endLocalDateTime: LocalDateTime? = LocalDateTime.now(),

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject? = TextValueObject(text="cost",value=777),//.apply { this.text="cost" }

    // 출발 위치 + 도착 위치
    var srcLocation: Location? = null,
    var dstLocation: Location? = Location("provider"),
    var srcPosition: LatLng? = LatLng(11.111,22.222),
    var dstPosition: LatLng? = LatLng(33.333,44.444),
    var srcAddress: String? = "srcAddress",
    var dstAddress: String? = "dstAddress",

    // 출발 예정 시각
    var departureLocalDateTime: LocalDateTime? = LocalDateTime.now(),

    // 알람 에정 시각
    var alarmLocalDateTime: LocalDateTime? = LocalDateTime.now(),

    // 제목
    var title: String? = "title",

    // 메모
    var memo: String? = "memo"
)