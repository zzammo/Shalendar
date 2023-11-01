package com.ddmyb.shalendar

import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.Group
import com.ddmyb.shalendar.domain.Schedule
import com.ddmyb.shalendar.domain.ScheduleDto
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

class manageSchedule {

    private lateinit var mFirebaseAuth: FirebaseAuth
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    private lateinit var mScheduleDatabaseRef: DatabaseReference //스케줄 데이터베이스
    private lateinit var mGroupDatabaseRef: DatabaseReference //그룹 데이터베이스

    fun Login(strEmail : String, strPwd : String, context: Context) {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseAuth!!.signInWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
    //현재 로그인한 유저의 개인 스케줄 생성
    fun createUserSchedule(curSc : ScheduleDto) {
        mFirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = mFirebaseAuth.currentUser
        mScheduleDatabaseRef = FirebaseDatabase.getInstance().getReference("Schedules")
        val newChildRef = mScheduleDatabaseRef.push()

        curSc.userId = currentUser!!.uid
        newChildRef.setValue(curSc)
        val Schedule_Id = newChildRef.key
        newChildRef.child("scheduleId").setValue(Schedule_Id)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UsersAccount")
        mDatabaseRef.child(currentUser!!.uid).child("Schedules").push().setValue(Schedule_Id)
        return
    }
    //현재 로그인한 유저가 포함된 그룹 생성
    fun createGroup(gName:String) {
        mFirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = mFirebaseAuth.currentUser
        mGroupDatabaseRef = FirebaseDatabase.getInstance().getReference("Groups")
        val newChildRef=mGroupDatabaseRef.push()
        newChildRef.setValue(groupinit(gName))
        val groupId = newChildRef.key.toString()
        mGroupDatabaseRef.child(groupId).child("groupId").setValue(groupId)
        mGroupDatabaseRef.child(groupId).child("userIds").push().setValue(currentUser!!.uid)
        return
    }
    // ID를 기반으로 유저 초대
    fun inviteGroup(){

    }
    // 그룹 스케줄 생성, 원래 그룹 이름이나 아이디 받아서 와야함
    fun crateGroupSchedule(curSc : ScheduleDto, groupId123: String) {
        mFirebaseAuth = FirebaseAuth.getInstance()
        val currentUser = mFirebaseAuth.currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Schedules")
        val newChildRef = mDatabaseRef.push()

        curSc.userId = currentUser!!.uid
        newChildRef.setValue(curSc)
        val Schedule_Id = newChildRef.key
        newChildRef.child("scheduleId").setValue(Schedule_Id)
        var groupId = "-Ni5KLKrA9_rDqow-Nku"
        mDatabaseRef =
            FirebaseDatabase.getInstance().getReference("Groups").child(groupId).child("Schedules")
        val newChildRef2 = mDatabaseRef.push()
        newChildRef2.setValue(Schedule_Id)
        return
    }
    fun readSchedule(curSc : ScheduleDto){
        return

    }
    fun updateSchedule(scheduleId : String, curSc: ScheduleDto) {

    }
}
fun parseLatLngFromString(str: String): LatLng? {
    try {
        // "lat/lng: (12.34,30.56)"와 같은 문자열을 파싱
        val startIndex = str.indexOf("(") + 1
        val endIndex = str.indexOf(")")
        val latLngStr = str.substring(startIndex, endIndex)
        val latLngParts = latLngStr.split(",")

        // 위도 및 경도 추출 및 LatLng 객체 생성
        val latitude = latLngParts[0].toDouble()
        val longitude = latLngParts[1].toDouble()

        return LatLng(latitude, longitude)
    } catch (e: Exception) {
        // 변환에 실패하면 null 반환
        return null
    }
}

class groupinit(gnn:String) {
    var gname: String = gnn
}