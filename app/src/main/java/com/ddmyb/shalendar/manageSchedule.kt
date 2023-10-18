package com.ddmyb.shalendar

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.domain.Schedule
import com.ddmyb.shalendar.domain.protoSchedule
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
    object mychildEventListener: ChildEventListener {

        var List1 = arrayListOf<Schedule>()
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
            val mSc: Schedule = Schedule()
            mSc.scheduleId = dataSnapshot.child("scheduleId").getValue(String::class.java)
            mSc.isPublic =
                dataSnapshot.child("startLocalDateTime").getValue(Boolean::class.java) == true
            mSc.userId = dataSnapshot.child("userId").getValue(Int::class.java)
            mSc.startLocalDateTime = LocalDateTime.parse(
                dataSnapshot.child("startLocalDateTime").getValue(String::class.java),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            )
            mSc.endLocalDateTime = LocalDateTime.parse(
                dataSnapshot.child("endLocalDateTime").getValue(String::class.java),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            )
            //mSc.meansType = MeansType.(dataSnapshot.child("meansType").getValue(Int::class.java))
            mSc.cost?.text =
                dataSnapshot.child("cost").child("text").getValue(String::class.java).toString()
            mSc.cost!!.value =
                dataSnapshot.child("cost").child("text").getValue(Int::class.java)!!
            mSc.srcPosition = dataSnapshot.child("srcPosition").getValue(String::class.java)
                ?.let { parseLatLngFromString(it) }
            mSc.dstPosition = dataSnapshot.child("dstPosition").getValue(String::class.java)
                ?.let { parseLatLngFromString(it) }
            mSc.srcAddress = dataSnapshot.child("dstAddress").getValue(String::class.java)
            mSc.dstAddress = dataSnapshot.child("dstAddress").getValue(String::class.java)

            mSc.departureLocalDateTime = LocalDateTime.parse(
                dataSnapshot.child("departureLocalDateTime").getValue(String::class.java),
                DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
            )

            mSc.title = dataSnapshot.child("title").getValue(String::class.java)
            mSc.memo = dataSnapshot.child("memo").getValue(String::class.java)
            List1.add(mSc)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }

    fun saveSchedule(curSc : Schedule) {
        val currentUser = mFirebaseAuth.currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UsersSchedule").child(currentUser!!.uid)
        mDatabaseRef.push().setValue(curSc)
        return;
    }
    fun loadSchedule(minValue:String,maxValue:String): List<Schedule> {
        val currentUser = mFirebaseAuth.currentUser
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UsersSchedule").child(currentUser!!.uid)
        val query1: Query = mDatabaseRef!!.orderByChild("startLocalDateTime").startAt(minValue)
        val query2: Query = query1.orderByChild("endLocalDateTime").endAt(maxValue)
        var List1 = arrayListOf<Schedule>()
        mDatabaseRef.child(currentUser.uid).addChildEventListener(object :ChildEventListener {

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val mSc: Schedule = Schedule()
                mSc.scheduleId = dataSnapshot.child("scheduleId").getValue(String::class.java)
                mSc.isPublic =
                    dataSnapshot.child("startLocalDateTime").getValue(Boolean::class.java) == true
                mSc.userId = dataSnapshot.child("userId").getValue(Int::class.java)
                mSc.startLocalDateTime = LocalDateTime.parse(
                    dataSnapshot.child("startLocalDateTime").getValue(String::class.java),
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                )
                mSc.endLocalDateTime = LocalDateTime.parse(
                    dataSnapshot.child("endLocalDateTime").getValue(String::class.java),
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                )
                //mSc.meansType = MeansType.(dataSnapshot.child("meansType").getValue(Int::class.java))
                mSc.cost?.text =
                    dataSnapshot.child("cost").child("text").getValue(String::class.java).toString()
                mSc.cost!!.value =
                    dataSnapshot.child("cost").child("text").getValue(Int::class.java)!!
                mSc.srcPosition = dataSnapshot.child("srcPosition").getValue(String::class.java)
                    ?.let { parseLatLngFromString(it) }
                mSc.dstPosition = dataSnapshot.child("dstPosition").getValue(String::class.java)
                    ?.let { parseLatLngFromString(it) }
                mSc.srcAddress = dataSnapshot.child("dstAddress").getValue(String::class.java)
                mSc.dstAddress = dataSnapshot.child("dstAddress").getValue(String::class.java)

                mSc.departureLocalDateTime = LocalDateTime.parse(
                    dataSnapshot.child("departureLocalDateTime").getValue(String::class.java),
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                )

                mSc.title = dataSnapshot.child("title").getValue(String::class.java)
                mSc.memo = dataSnapshot.child("memo").getValue(String::class.java)
                List1.add(mSc)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return List1
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



