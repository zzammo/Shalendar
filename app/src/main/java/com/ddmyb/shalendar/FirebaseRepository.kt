package com.ddmyb.shalendar

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.domain.ScheduleDto
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseRepository {

    private val mFirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var mDatabaseRef: DatabaseReference //실시간 데이터베이스
    private lateinit var mScheduleDatabaseRef: DatabaseReference //스케줄 데이터베이스
    private lateinit var mGroupDatabaseRef: DatabaseReference //그룹 데이터베이스
    private lateinit var mChildbaseRef: DatabaseReference //그룹 데이터베이스

    companion object{
        private var instance:FirebaseRepository? = null
        @Synchronized
        fun getInstance(): FirebaseRepository?{
            if (instance == null){
                synchronized(FirebaseRepository::class){
                    instance = FirebaseRepository()
                }
            }
            return instance
        }
    }

    fun logout(){
        mFirebaseAuth.signOut()
    }
    fun checkLogin():Boolean{
        val currentUser = mFirebaseAuth!!.currentUser
        Log.d("checkLogin", currentUser.toString())
        return currentUser!=null
    }

    fun getUserId():String{
        val currentUser = mFirebaseAuth!!.currentUser
        if(currentUser!=null)
            return currentUser.uid
        else return "NULL"
    }

    fun login(strEmail: String, strPwd: String, context: Context, activity: AppCompatActivity) {
        mFirebaseAuth!!.signInWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                    activity.setResult(AppCompatActivity.RESULT_OK)
                    activity.finish()
                } else {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }
    //현재 로그인한 유저의 개인 스케줄 생성
    fun createUserSchedule(scheduleDto: ScheduleDto) {
        Log.d("createUserSchedule", "start")
        val currentUser = mFirebaseAuth.currentUser
        mScheduleDatabaseRef = FirebaseDatabase.getInstance().getReference("Schedule")
        val newChildRef = mScheduleDatabaseRef.push()

        scheduleDto.userId = currentUser!!.uid
        newChildRef.setValue(scheduleDto)
        val scheduleId = newChildRef.key
        newChildRef.child("scheduleId").setValue(scheduleId)
        Log.d("createUserSchedule", "scheduleId: "+ scheduleId)

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UserAccount")
        mDatabaseRef.child(currentUser!!.uid).child("Schedule").push().setValue(scheduleId)
        Log.d("createUserSchedule", "end")
    }
    //현재 로그인한 유저가 포함된 그룹 생성
    fun createGroup(gName: String) {
        val currentUser = mFirebaseAuth.currentUser
        mGroupDatabaseRef = FirebaseDatabase.getInstance().getReference("Group")
        val newChildRef = mGroupDatabaseRef.push()
        //group생성
        newChildRef.setValue(groupinit(gName))
        val groupId = newChildRef.key.toString()
        //그룹에 groupId넣기
        mGroupDatabaseRef.child(groupId).child("groupId").setValue(groupId)
        //그룹에 userId넣기
        mGroupDatabaseRef.child(groupId).child("userId").push().setValue(currentUser!!.uid)
        //user에 groupId 넣기
        mDatabaseRef =
            FirebaseDatabase.getInstance().getReference("UserAccount").child(currentUser!!.uid)
                .child("groupId")
        mDatabaseRef.setValue(newChildRef.key.toString())
        return
    }

    // groupID를 기반으로 접속한 유저 초대
    fun inviteGroup(groupId: String) {
        val currentUser = mFirebaseAuth.currentUser
        mGroupDatabaseRef =
            FirebaseDatabase.getInstance().getReference("Group").child(groupId).child("userId")
        //그룹에 userId추가
        mGroupDatabaseRef.push().setValue(currentUser!!.uid)
        //user에 groupId추가
        mDatabaseRef =
            FirebaseDatabase.getInstance().getReference("UserAccount").child(currentUser!!.uid)
                .child("groupId")
        mDatabaseRef.push().setValue(groupId)
    }

    // 그룹 스케줄 생성, 그룹 아이디 받아와서 생성
    fun createGroupSchedule(curSc: ScheduleDto, groupId: String) {
        val currentUser = mFirebaseAuth.currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Schedule")
        val newChildRef = mDatabaseRef.push()
        //스케줄에 스케줄 생성
        curSc.userId = currentUser!!.uid
        curSc.groupId = groupId
        newChildRef.setValue(curSc)
        val Schedule_Id = newChildRef.key
        newChildRef.child("scheduleId").setValue(Schedule_Id)
        //그룹에 스케줄아이디 생성
        mDatabaseRef =
            FirebaseDatabase.getInstance().getReference("Group").child(groupId)
                .child("groupSchedule")
        mDatabaseRef.push().setValue(Schedule_Id)
        return
    }
    //listener : ChildEventListener
    val myList = ArrayList<ScheduleDto>()
    fun readUserSchedule() {
        val currentUser = mFirebaseAuth.currentUser
        mDatabaseRef =
            FirebaseDatabase.getInstance().getReference("UserAccount").child(currentUser!!.uid).child("Schedule")
        mDatabaseRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.e("dorimaengdol", "ChildEventListener-onChildAdded : ${snapshot.value}")
                val scheduleId = snapshot.value.toString()
                mChildbaseRef=FirebaseDatabase.getInstance().getReference("Schedule").child(scheduleId)
                if(true) {
                    mChildbaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                val data = dataSnapshot.getValue(ScheduleDto::class.java)
                                if (data != null) {
                                    Log.d("dorimaengdol", data.scheduleId.toString())
                                }
                            } else {
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
                else{   //위에 보고 따라하기 가능
                    //mChildbaseRef.addListenerForSingleValueEvent()
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun readGroupUser(groupId: String) {
        val currentUser = mFirebaseAuth.currentUser
        mDatabaseRef =
            FirebaseDatabase.getInstance().getReference("UserAccount").child(currentUser!!.uid)
                .child("Schedule")
        mDatabaseRef.get()
    }
    fun updateSchedule(scheduleId : String, curSc: ScheduleDto) {

    }
    fun deleteSchedule(scheduleId : String){

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
    var groupName: String = gnn
}