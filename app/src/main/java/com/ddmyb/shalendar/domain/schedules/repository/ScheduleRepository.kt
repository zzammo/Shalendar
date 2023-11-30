package com.ddmyb.shalendar.domain.schedules.repository

import android.util.Log
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.schedules.Schedule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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

    suspend fun readUserAllSchedule(): List<ScheduleDto>{
        val uID = firebaseAuth.currentUser!!.uid
        init()
        val allScheduleId = (userSnapshot.child(uID).child("scheduleId").children.mapNotNull { it.key } +
                userSnapshot.child(uID).child("groupId").children.flatMap { groupId ->
                    groupSnapshot.child(groupId.key!!).child("scheduleId").children.mapNotNull { it.key }
                }).distinct()

        return allScheduleId.map {
            scheduleSnapshot.child(it).getValue(ScheduleDto::class.java)!!
        }
    }

    suspend fun readOneSchedule(scheduleId : String): ScheduleDto {
        return scheduleRef.child(scheduleId).get().await().getValue(ScheduleDto::class.java)!!
    }

    suspend fun readGroupSchedule(groupId: String): MutableList<ScheduleDto>{
        init()
        val uID = firebaseAuth.currentUser!!.uid

        return (groupSnapshot.child(groupId).child("userId").children.flatMap { userId ->
            userSnapshot.child(userId.key!!).child("scheduleId").children.map { scheduleId ->
                scheduleSnapshot.child(scheduleId.key!!).getValue(ScheduleDto::class.java)!!
            }
        } + groupSnapshot.child(groupId).child("scheduleId").children.map { scheduleId ->
            scheduleSnapshot.child(scheduleId.key!!).getValue(ScheduleDto::class.java)!!
        } + userSnapshot.child(uID).child("groupId").children.flatMap { id ->
            if (groupId != id.key!!) {
                groupSnapshot.child(id.key!!).child("scheduleId").children.map { scheduleId ->
                    val scheduleDto = scheduleSnapshot.child(scheduleId.key!!).getValue(ScheduleDto::class.java)!!
                    scheduleDto.groupId =  groupSnapshot.child(id.key!!).child("groupName").getValue(String::class.java)!!
                    scheduleDto.color = R.color.olive_green
                    scheduleDto
                }
            }else{
                listOf()
            }
        }).toMutableList()
    }

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
