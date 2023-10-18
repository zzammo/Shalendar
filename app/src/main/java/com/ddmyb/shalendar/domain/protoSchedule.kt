package com.ddmyb.shalendar.domain

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
data class protoSchedule (
    var scheduleId: String? = "scheduleId",
    var isPublic: Boolean = false,
    var userId: Int? = 2019115204,

    // 일정 시작 시간
    var startLocalDateTime: String? = "startLocalDateTime",
    var endLocalDateTime: String? = "endLocalDateTime",

    // 소요 시간
    var meansType: MeansType = MeansType.NULL,
    var cost: TextValueObject? = TextValueObject(text = "text",value = 10),

    // 출발 위치 + 도착 위치
    var srcPosition: String? = "srcPosition",
    var dstPosition: String? = "dstPosition",
    var srcAddress: String? = "srcAddress",
    var dstAddress: String? = "dstAddress",

    // 출발 예정 시각
    var departureLocalDateTime: String? = "departureLocalDateTime",

    // 제목
    var title: String? = "title",

    // 메모
    var memo: String? = "memo"
)