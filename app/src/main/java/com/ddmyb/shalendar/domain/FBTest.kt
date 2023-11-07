package com.ddmyb.shalendar.domain

import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

object FBTest {

    private const val SCHEDULE_REF = "Schedule"
    private const val GROUP_REF = "Group"
    private const val USER_REF = "UserAccount"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val scheduleRef = FirebaseDatabase.getInstance().getReference(SCHEDULE_REF)
    private val groupRef = FirebaseDatabase.getInstance().getReference(GROUP_REF)
    private val userRef = FirebaseDatabase.getInstance().getReference(USER_REF)

    fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }

    suspend fun readUserSchedules(uid: String): List<ScheduleDto> {
        val currentUserUid = firebaseAuth.currentUser?.uid ?: return listOf()

        val scheduleIdListSS = userRef.child(uid).child("Schedule")
        val scheduleIdList = mutableListOf<String>()
        for (scheduleIdSS in scheduleIdListSS.get().await().children) {
            val scheduleId = scheduleIdSS.key
            scheduleIdList.add(scheduleId!!)
        }
        val scheduleList = mutableListOf<ScheduleDto>()
        for (scheduleId in scheduleIdList) {
            scheduleList.add(scheduleRef.child(scheduleId).get().await().getValue(ScheduleDto::class.java)!!)
        }

        return scheduleList
    }

    suspend fun readGroupSchedule(groupId: String): List<ScheduleDto> {
        val scheduleList = mutableListOf<ScheduleDto>()
        for (scheduleId in groupRef.child(groupId).child("Schedule").get().await().children) {
            scheduleList.add(scheduleRef.child(scheduleId.key!!).get().await().getValue(ScheduleDto::class.java)!!)
        }
        return scheduleList
    }




}