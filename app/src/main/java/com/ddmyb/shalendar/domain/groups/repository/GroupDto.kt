package com.ddmyb.shalendar.domain.groups.repository

import com.ddmyb.shalendar.R
import java.time.ZoneId

data class GroupDto (
    var groupName: String = "",
    var groupId: String = "", // 개인 일정일 경우 "", 그룹 일정일 경우 그룹 id
    var memberCnt: Int = 0,
    var latestUpdateMills: Long = 0L
)