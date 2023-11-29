package com.ddmyb.shalendar.domain.users

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserRepository {

    private val SCHEDULE_REF = "Schedule"
    private val GROUP_REF = "Group"
    private val USER_REF = "UserAccount"

    private val firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private var instance: UserRepository? = null

        @Synchronized
        fun getInstance(): UserRepository? {
            if (instance == null) {
                synchronized(UserRepository::class) {
                    instance = UserRepository()
                }
            }
            return instance
        }
    }

    fun signup(strNickname: String, strEmail: String, strPwd: String) {
        return
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun checkLogin(): Boolean {
        val currentUser = firebaseAuth!!.currentUser
        Log.d("checkLogin", currentUser.toString())
        return currentUser != null
    }

    fun getUserId(): String {
        val currentUser = firebaseAuth!!.currentUser
        if (currentUser != null)
            return currentUser.uid
        else return "NULL"
    }

    fun login(strEmail: String, strPwd: String, context: Context) {
        firebaseAuth!!.signInWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
//    fun parseLatLngFromString(str: String): LatLng? {
//        try {
//            // "lat/lng: (12.34,30.56)"와 같은 문자열을 파싱
//            val startIndex = str.indexOf("(") + 1
//            val endIndex = str.indexOf(")")
//            val latLngStr = str.substring(startIndex, endIndex)
//            val latLngParts = latLngStr.split(",")
//
//            // 위도 및 경도 추출 및 LatLng 객체 생성
//            val latitude = latLngParts[0].toDouble()
//            val longitude = latLngParts[1].toDouble()
//
//            return LatLng(latitude, longitude)
//        } catch (e: Exception) {
//            // 변환에 실패하면 null 반환
//            return null
//        }
//    }