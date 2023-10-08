package com.ddmyb.shalendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class Schedule (
    var scheduleId: String? = null,
    var isPublic: Boolean = false,
    var userId: Int? = null,

    // 일정 시작 시간
    var startLocalDateTime: LocalDateTime? = null,
    var endLocalDateTime: LocalDateTime? = null,

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject? = null,

    // 출발 위치 + 도착 위치
    var srcPosition: LatLng? = null,
    var dstPosition: LatLng? = null,
    var srcAddress: String? = null,
    var dstAddress: String? = null,

    // 출발 예정 시각
    var departureLocalDateTime: LocalDateTime? = null,

    // 제목
    var title: String? = null,

    // 메모
    var memo: String? = null
)