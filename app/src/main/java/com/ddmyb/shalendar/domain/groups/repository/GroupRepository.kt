package com.ddmyb.shalendar.domain.groups.repository

import com.ddmyb.shalendar.domain.groups.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId


class GroupRepository {

    private val SCHEDULE_REF = "Schedule"
    private val GROUP_REF = "Group"
    private val USER_REF = "UserAccount"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val scheduleRef = FirebaseDatabase.getInstance().getReference(SCHEDULE_REF)
    private val groupRef = FirebaseDatabase.getInstance().getReference(GROUP_REF)
    private val userRef = FirebaseDatabase.getInstance().getReference(USER_REF)

    companion object {
        private var instance: GroupRepository? = null
        @Synchronized
        fun getInstance(): GroupRepository? {
            if (instance == null) {
                synchronized(GroupRepository::class) {
                    instance = GroupRepository()
                }
            }
            return instance
        }
    }

    suspend fun readUsersGroup(): MutableList<GroupDto> {
        val uID = firebaseAuth.currentUser!!.uid
        val groupIdList: MutableList<String> = mutableListOf()
        for (curgroupIds in userRef.child(uID).child("groupId").get().await().children) {
            groupIdList.add(curgroupIds.key.toString())
        }

        val groupList: MutableList<GroupDto> = mutableListOf()
        for (groupId in groupIdList) {
            var curGroup: GroupDto = GroupDto()
            curGroup.groupId = groupId
            curGroup.groupName = groupRef.child(groupId).child("groupName").get().await().getValue(String::class.java).toString()
            curGroup.memberCnt = groupRef.child(groupId).child("memberCnt").get().await().getValue(Int::class.java)!!.toInt()

            for (curUserId1 in groupRef.child(groupId).child("userId").get().await().children) {
                var curUserId = curUserId1.key.toString()
                curGroup.userId.add(userRef.child(curUserId).child("nickName").get().await().getValue(String::class.java).toString())
            }
            groupList.add(curGroup)
        }
        return groupList
    }
    fun createGroup(gName: String): String {
        val uID = firebaseAuth.currentUser!!.uid
        val newChildRef = groupRef.push()
        val newGroupId = newChildRef.key.toString()
        val newGroupDto = GroupDto(Group(gName, newGroupId, 1))
        newChildRef.setValue(newGroupDto)
        newChildRef.child("userId").child(uID).setValue(uID)
        userRef.child(uID).child("groupId").child(newGroupId).setValue(newGroupId)
        return newGroupId
    }

    // groupID를 기반으로 접속한 유저 초대
    suspend fun inviteGroup(groupId: String) {
        groupRef.child(groupId).child("latestUpdateMills").setValue(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000)

        val uID = firebaseAuth.currentUser!!.uid
        groupRef.child(groupId).child("userId").child(uID).setValue(uID) //그룹에 userId추가
        var memberCnt = groupRef.child(groupId).child("memberCnt").get().await().getValue(Int::class.java)?.plus(1)
        groupRef.child(groupId).child("memberCnt").setValue(memberCnt)
        userRef.child(uID).child("groupId").child(groupId).setValue(groupId) //user에 groupId추가
    }

    fun deleteGroup(groupId: String) {

    }

    fun deleteEntireGroup(groupId: String) {

    }

}