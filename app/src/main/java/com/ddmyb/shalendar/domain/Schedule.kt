package com.ddmyb.shalendar.domain

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class
Schedule (
    var scheduleId: String? = "scheduleId",
    var isPublic: Boolean = false,
    var userId: Int? = 201915204,

    // 일정 시작 시간
    var startLocalDateTime: LocalDateTime? = LocalDateTime.now(),
    var endLocalDateTime: LocalDateTime? = LocalDateTime.now(),

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject? = TextValueObject(text = "text",value = 10),

    // 출발 위치 + 도착 위치
    var srcPosition: LatLng? = LatLng(12.34,30.56),
    var dstPosition: LatLng? = LatLng(12.34,30.56),
    var srcAddress: String? = "srcAddress",
    var dstAddress: String? = "dstAddress",

    // 출발 예정 시각
    var departureLocalDateTime: LocalDateTime? = LocalDateTime.now(),

    // 제목
    var title: String? = "title",

    // 메모
    var memo: String? = "memo"

)