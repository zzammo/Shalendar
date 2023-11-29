package com.ddmyb.shalendar.domain.schedules.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.ZoneId

class ScheduleRepository {
    private val SCHEDULE_REF = "Schedule"
    private val GROUP_REF = "Group"
    private val USER_REF = "UserAccount"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val scheduleRef = FirebaseDatabase.getInstance().getReference(SCHEDULE_REF)
    private val groupRef = FirebaseDatabase.getInstance().getReference(GROUP_REF)
    private val userRef = FirebaseDatabase.getInstance().getReference(USER_REF)

    companion object {
        private var instance: ScheduleRepository? = null
        @Synchronized
        fun getInstance(): ScheduleRepository? {
            if (instance == null) {
                synchronized(ScheduleRepository::class) {
                    instance = ScheduleRepository()
                }
            }
            return instance
        }
    }
    //현재 로그인한 유저의 개인 스케줄 생성
    fun createUserSchedule(curSc: ScheduleDto) {
        val uID = firebaseAuth.currentUser!!.uid
        val newChildRef = scheduleRef.push()
        val scheduleId = newChildRef.key.toString()
        curSc.userId = uID    //이거 필요한지 나중에 확인
        curSc.scheduleId = scheduleId
        newChildRef.setValue(curSc)
        userRef.child(uID).child("scheduleId").child(scheduleId).setValue(scheduleId)
        return
    }
    fun createGroupSchedule(curSc: ScheduleDto, groupId: String) {
        groupRef.child(groupId).child("latestUpdateMills").setValue(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000)

        val newChildRef = scheduleRef.push()
        val scheduleId = newChildRef.key.toString()
        //스케줄에 스케줄 생성, 얘네 밑에 필요 없다는데 확인해보기
        curSc.scheduleId = scheduleId
        curSc.userId = firebaseAuth.currentUser!!.uid
        curSc.groupId = groupId
        newChildRef.setValue(curSc)

        //그룹에 스케줄아이디 생성
        val updateTime: Long = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000
        groupRef.child(groupId).child("latestUpdateMills").setValue(updateTime)
        groupRef.child(groupId).child("scheduleId").child(scheduleId).setValue(scheduleId)
        return
    }

    suspend fun readUserSchedule(): List<ScheduleDto> {
        val uID = firebaseAuth.currentUser!!.uid

        val scheduleIdList = mutableListOf<String>()
        for (curscheduleId in userRef.child(uID).child("scheduleId").get().await().children) {
            val scheduleId = curscheduleId.key
            scheduleIdList.add(scheduleId!!)
        }
        val scheduleList = mutableListOf<ScheduleDto>()
        for (scheduleId in scheduleIdList) {
            scheduleList.add(scheduleRef.child(scheduleId).get().await().getValue(ScheduleDto::class.java)!!)
        }

        return scheduleList
    }

    suspend fun readUserAllSchedule(): List<ScheduleDto> {
        val uID = firebaseAuth.currentUser!!.uid

        val scheduleIdList = mutableListOf<String>()
        for (curscheduleId in userRef.child(uID).child("scheduleId").get().await().children) {
            scheduleIdList.add(curscheduleId.key.toString())
        }

        val GroupIdList = mutableListOf<String>()
        for (curgroupId in userRef.child(uID).child("groupId").get().await().children) {
            val curGroupId = curgroupId.key.toString()
            for (curscheduleId in groupRef.child(curGroupId).child("scheduleId").get().await().children) {
                scheduleIdList.add(curscheduleId.key.toString())
            }
        }
        val scheduleList = mutableListOf<ScheduleDto>()
        for (scheduleId in scheduleIdList) {
            scheduleList.add(scheduleRef.child(scheduleId).get().await().getValue(ScheduleDto::class.java)!!)
        }
        return scheduleList
    }
    suspend fun readOneSchedule(scheduleId : String): ScheduleDto {
        return scheduleRef.child(scheduleId).get().await().getValue(ScheduleDto::class.java)!!
    }
    suspend fun readGroupSchedule(groupId: String): MutableList<ScheduleDto> {

        val userIdList: MutableList<String> = mutableListOf()
        for (curUserIds in groupRef.child(groupId).child("userId").get().await().children) {
            userIdList.add(curUserIds.key.toString())
        }

        val scheduleIdList: MutableList<String> = mutableListOf()
        for (userId in userIdList) {
            for (curScheduleIds in userRef.child(userId).child("scheduleId").get().await().children) {
                scheduleIdList.add(curScheduleIds.key.toString())
            }
        }

        val scheduleList: MutableList<ScheduleDto> = mutableListOf()
        for (scheduleId in scheduleIdList) {
            scheduleList.add(scheduleRef.child(scheduleId).get().await().getValue(ScheduleDto::class.java)!!)
        }

        //아래는 그룹 스케줄
        for (scheduleId in groupRef.child(groupId).child("scheduleId").get().await().children) {
            scheduleList.add(scheduleRef.child(scheduleId.key!!).get().await().getValue(ScheduleDto::class.java)!!)
        }

        return scheduleList
    }
    //해당 그룹에 속한 user들의 ID반환
//    suspend fun readGroupUser(groupId: String): MutableList<String> {
//        val userIds: MutableList<String> = mutableListOf()
//
//        for (curUserIds in groupRef.child(groupId).child("userId").get().await().children) {
//            val curUserId = curUserIds.key
//            userIds.add(curUserId!!)
//        }
//        return userIds
//    }
    fun updateSchedule(curSc: ScheduleDto) {
        scheduleRef.child(curSc.scheduleId).setValue(curSc)
        if (curSc.groupId == "") {
            userRef.child(curSc.userId).child("scheduleId").child(curSc.scheduleId).removeValue()
            userRef.child(curSc.userId).child("scheduleId").child(curSc.scheduleId).setValue(curSc.scheduleId)
        }
        else {
            groupRef.child(curSc.groupId).child("latestUpdateMills").setValue(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000)

            groupRef.child(curSc.groupId).child("scheduleId").child(curSc.scheduleId).removeValue()
            groupRef.child(curSc.groupId).child("scheduleId").child(curSc.scheduleId).setValue(curSc.scheduleId)
        }
    }

    fun deleteSchedule(curSc: ScheduleDto) {
        scheduleRef.child(curSc.scheduleId).removeValue()
        if (curSc.groupId == "") {
            userRef.child(curSc.userId).child("scheduleId").child(curSc.scheduleId).removeValue()
        } else {
            groupRef.child(curSc.groupId).child("latestUpdateMills").setValue(LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000)
            groupRef.child(curSc.groupId).child("scheduleId").child(curSc.scheduleId).removeValue()
        }
    }

}
