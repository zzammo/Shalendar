package com.ddmyb.shalendar.domain.groups.repository

import android.util.Log
import com.ddmyb.shalendar.domain.groups.Group
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    private lateinit var groupSnapshot: DataSnapshot
    private lateinit var userSnapshot: DataSnapshot
    private lateinit var scheduleSnapshot: DataSnapshot


    private suspend fun init() {
        val snapshot = FirebaseDatabase.getInstance().reference.get().await()
        groupSnapshot = snapshot.child(GROUP_REF)
        userSnapshot = snapshot.child(USER_REF)
        scheduleSnapshot = snapshot.child(SCHEDULE_REF)
    }

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
        init()

        val uID = firebaseAuth.currentUser!!.uid
        val groupIdList = userSnapshot.child(uID).child("groupId").children.map {
            Log.d("groupId", it.key!!)
            it.key!!
        }

        val groupList = mutableListOf<GroupDto>()
        groupIdList.map {
            val groupDto = GroupDto()
            val tmp = groupSnapshot.child(it)
            groupDto.groupId = tmp.child("groupId").getValue(String::class.java)!!
            groupDto.groupName = tmp.child("groupName").getValue(String::class.java)!!
            groupDto.memberCnt = tmp.child("memberCnt").getValue(Int::class.java)!!
            groupDto.latestUpdateMills= tmp.child("latestUpdateMills").getValue(Long::class.java)!!
            userSnapshot.child("userId").children.map { userId ->
                groupDto.userId.add(userSnapshot.child(userId.key!!).child("nickName").getValue(String::class.java)!!)
            }
            Log.d("mapped_groupDto", groupDto.toString())
            groupList.add(groupDto)
        }

        return groupList
    }
    fun checkGroup(groupId: String, callback: (Boolean) -> Unit) {
        val query: Query = groupRef.child(groupId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // 데이터가 있으면 true, 없으면 false 전달
                callback(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 쿼리 중 오류 발생
                callback(false)
            }
        })
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

    suspend fun withdrawGroup(groupId: String) {
        val uID = firebaseAuth.currentUser!!.uid
        groupRef.child(groupId).child(uID).removeValue()

        var memberCnt = groupRef.child(groupId).child("memberCnt").get().await().getValue(Int::class.java)?.plus(-1)
        groupRef.child(groupId).child("memberCnt").setValue(memberCnt)

        userRef.child(uID).child("groupId").child(groupId).removeValue()
    }

    suspend fun deleteGroup(groupId: String) {
        val uID = firebaseAuth.currentUser!!.uid
        val userIdList: MutableList<String> = mutableListOf()
        val scheduleIdList: MutableList<String> = mutableListOf()
        groupRef.child(groupId).child(uID).removeValue()

        for (curUserId in groupRef.child(groupId).child("userId").get().await().children) {
            userIdList.add(curUserId.key.toString())
        }
        for (userId in userIdList) {
            userRef.child(userId).child("groupId").child(groupId).removeValue()
        }
        for (curScheduleId in groupRef.child(groupId).child("scheduleId").get().await().children) {
            scheduleIdList.add(curScheduleId.key.toString())
        }
        for (curScheduleId in scheduleIdList) {
            scheduleRef.child(curScheduleId).removeValue()
        }
        groupRef.child(groupId).removeValue()
    }
}