package com.ddmyb.shalendar.domain.users

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val SCHEDULE_REF = "Schedule"
    private val GROUP_REF = "Group"
    private val USER_REF = "UserAccount"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userRef = FirebaseDatabase.getInstance().getReference(USER_REF)

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

    fun login(strEmail: String, strPwd: String, context: Context, successJob:()->Unit) {
        firebaseAuth!!.signInWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                    successJob()
                } else {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun uploadImage(imageUri: Uri?) {
        val user = firebaseAuth!!.currentUser
        val storRef = FirebaseStorage.getInstance().reference
        Log.d("maengedol", "성공1")
        if (user != null && imageUri != null) {
            Log.d("maengedol", "성공2")
            val imageRef = storRef.child("pfImage/${user.email}.jpg")

            val uploadTask = imageRef.putFile(imageUri)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let  {
                        throw it
                    }
                }
                imageRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri: Uri? = task.result
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUri)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                userRef.child(user.uid).child("pfImageUrl").setValue(downloadUri)
                                Log.d("maengedol", "성공3")
                            } else {
                                // 프로필 업데이트 실패.
                                Log.d("maengedol", "실패1")
                            }
                        }
                } else {
                    // 이미지 업로드 실패.
                    Log.d("maengedol", "실패2")
                }
            }
        }
    }

    suspend fun readUserNickName(): String {
        return  userRef.child(firebaseAuth.currentUser!!.uid).child("nickName").get().await().getValue(String::class.java)!!
    }
    fun downloadImage(imageUri: Uri?) {
//        val storageRef = FirebaseStorage.getInstance().reference
//        val user = firebaseAuth!!.currentUser
//        val imagesRef = storageRef.child("pfImage/${user!!.email}.jpg")
//        Log.d("maengdol", "찐시작")
//        imagesRef.downloadUrl
//            .addOnSuccessListener { uri ->
//                imageUri = uri
//                // 다운로드 URL을 통해 이미지를 표시
//            }
//            .addOnFailureListener { exception ->
//                // 다운로드 실패 시
//                Log.e("FirebaseExample", "이미지 다운로드 실패", exception)
//            }
//
//        imagesRef.getFile(imageUri!!).addOnSuccessListener {
//            Log.d("maengdol","찐성공123")
//        }
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