package com.ddmyb.shalendar.domain.groups.repository

import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.groups.Group
import java.time.Instant
import java.time.ZoneId

data class GroupDto (
    var groupName: String = "",
    var groupId: String = "", // 개인 일정일 경우 "", 그룹 일정일 경우 그룹 id
    var memberCnt: Int = 0,
    var latestUpdateMills: Long = 0L,
    var userId: MutableList<String> = mutableListOf(),
    var pfImage: String = ""
) {
    constructor(Group: Group) : this() {
        this.groupName = Group.groupName
        this.groupId = Group.groupId
        this.memberCnt = Group.memberCnt
        this.userId = Group.userId
        this.latestUpdateMills = Group.latestUpdateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        this.pfImage = Group.pfImage
    }
}