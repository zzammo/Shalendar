package com.ddmyb.shalendar.domain.users

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.common.io.Files.getFileExtension
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class UserRepository {

    private val SCHEDULE_REF = "Schedule"
    private val GROUP_REF = "Group"
    private val USER_REF = "UserAccount"
    private val STOR_REF = "profImage"

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val scheduleRef = FirebaseDatabase.getInstance().getReference(SCHEDULE_REF)
    private val groupRef = FirebaseDatabase.getInstance().getReference(GROUP_REF)
    private val userRef = FirebaseDatabase.getInstance().getReference(USER_REF)
    private val storRef = FirebaseStorage.getInstance().getReference(STOR_REF)

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

    fun login(strEmail: String, strPwd: String, context: Context): Boolean {
        var loginFlag: Boolean = false
        firebaseAuth!!.signInWithEmailAndPassword(strEmail, strPwd)
            .addOnCompleteListener() { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show();
                    loginFlag = true
                } else {
                    Toast.makeText(context, "로그인 실패", Toast.LENGTH_SHORT).show()
                }
            }
        return loginFlag
    }

//    private fun uploadImage(imageUri: Uri?) {
//        val TAG = "maengdol"
//        val user = firebaseAuth.currentUser
//
//        val storageRef = storage.reference
//        Log.d(TAG, "성공")
//        if (user != null && imageUri != null) {
//            Log.d(TAG, "성공")
//            val imageRef = storageRef.child("profile_images/${user.email}.jpg")
//
//            val uploadTask = imageRef.putFile(imageUri)
//            uploadTask.continueWithTask { task ->
//                if (!task.isSuccessful) {
//                    task.exception?.let {
//                        throw it
//                    }
//                }
//                imageRef.downloadUrl
//            }.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val downloadUri: Uri? = task.result
//                    val profileUpdates = UserProfileChangeRequest.Builder()
//                        .setPhotoUri(downloadUri)
//                        .build()
//
//                    user.updateProfile(profileUpdates)
//                        .addOnCompleteListener { updateTask ->
//                            if (updateTask.isSuccessful) {
//                                // 프로필 업데이트 성공.
//                                storage.reference.child("profile_images/${user.email}.jpg").downloadUrl.addOnSuccessListener { imageUrl ->
//                                    Glide.with(this)
//                                        .load(imageUrl)
//                                        .into(mUserImage)
//                                }
//                                Log.d(TAG, "성공")
//                            } else {
//                                // 프로필 업데이트 실패.
//                                Log.d(TAG, "실패1")
//                            }
//                        }
//                } else {
//                    // 이미지 업로드 실패.
//                    Log.d(TAG, "실패2")
//                }
//            }
//        }
//    }

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