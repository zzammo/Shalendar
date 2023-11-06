package com.ddmyb.shalendar.domain.groups

import com.ddmyb.shalendar.R
import java.time.LocalDateTime
import java.time.ZoneId

data class Group (
    var groupName: String = "",
    var groupId: String = "", // 개인 일정일 경우 "", 그룹 일정일 경우 그룹 id
    var memberCnt: Int = 0,
    var latestUpdateMills: LocalDateTime = LocalDateTime.now()
)