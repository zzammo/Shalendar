package com.ddmyb.shalendar.domain.groups

import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.groups.repository.GroupDto
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

data class Group (
    var groupName: String = "",
    var groupId: String = "", // 개인 일정일 경우 "", 그룹 일정일 경우 그룹 id
    var memberCnt: Int = 0,
    var latestUpdateTime: LocalDateTime = LocalDateTime.now(),
    var userId: MutableList<String> = mutableListOf(),
    var pfImage: String = ""
) {
    constructor(GroupDto: GroupDto) : this() {
        this.groupName = GroupDto.groupName
        this.groupId = GroupDto.groupId
        this.memberCnt = GroupDto.memberCnt
        this.userId = GroupDto.userId
        this.latestUpdateTime = Instant.ofEpochMilli(GroupDto.latestUpdateMills).atZone(ZoneId.systemDefault()).toLocalDateTime()
        this.pfImage = GroupDto.pfImage
    }
}