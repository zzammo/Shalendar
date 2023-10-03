package com.ddmyb.shalendar.domain

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.distance.model.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Schedule (
    private var scheduleId: String? = null,

    var startLocalDateTime: LocalDateTime? = null,
    var endLocalDateTime: LocalDateTime? = null,

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject? = null,

    // 출발 위치 + 도착 위치
    var srcLocation: Location? = null,
    var dstLocation: Location? = null,
    var srcPosition: LatLng? = null,
    var dstPosition: LatLng? = null,
    var srcAddress: String? = null,
    var dstAddress: String? = null,

    // 출발 예정 시간
    var departureLocalDateTime: LocalDateTime? = null,

    var alarm: String? = null,
    var memo: String? = null
)