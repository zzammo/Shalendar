package com.ddmyb.shalendar.view.home.navidrawer

import ToggleAnimation
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.login.LoginActivity
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.domain.FBTest
import com.ddmyb.shalendar.domain.groups.repository.GroupRepository
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleRepository
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.view.home.navidrawer.adapter.OwnedCalendarAdapter
import com.ddmyb.shalendar.view.login.ChangePwdActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import com.bumptech.glide.Glide
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class NaviDrawerActivity :AppCompatActivity() {

    private var fbAuth: FirebaseAuth? = null
    private var userRef: DatabaseReference? = null //실시간 데이터베이스
    private var mtvName: TextView? = null
    private var adapter2: ArrayAdapter<String>? = null

    private var sclistView: ListView? = null

    private lateinit var selectedImageUri: Uri
    private lateinit var imagePicker: ActivityResultLauncher<Intent>

    private val userRepository = UserRepository.getInstance()

    private val binding by lazy {
        NaviDrawerBinding.inflate(layoutInflater)
    }
    private val viewModel by lazy {
        ViewModelProvider(this)[NaviViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mtvName = binding.tvName
        fbAuth = FirebaseAuth.getInstance()
        userRef = FirebaseDatabase.getInstance().getReference("UsersSchedule")
        val currentUser = fbAuth!!.currentUser

        binding.btnAddSc.setOnClickListener {
            var imageuri: Uri? = null
            userRepository!!.downloadImage(imageuri)
        }



        binding.btnCheckSc.setOnClickListener {
            val mSc = ScheduleDto()
            GroupRepository().createGroup("도리맹돌의 수하물들")

            CoroutineScope(Dispatchers.IO).launch {
                val scheduleList = FBTest.readUserSchedule(FBTest.getCurrentUserUid()!!)
                for (schedule in scheduleList) {
                    Log.d("Dirtfy Test", schedule.scheduleId)
                }
            }

            //manageSchedule().crateGroupSchedule(mSc,"-Ni8tG4kZmQCO1W0JBzk")
//            manageSchedule().inviteGroup("-Ni8tG4kZmQCO1W0JBzk")
        }

        imagePicker = this.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                selectedImageUri = data?.data!!

                // 이미지 업로드 및 프로필 업데이트 작업 수행
                if (selectedImageUri != null) {
                    userRepository!!.uploadImage(selectedImageUri)
                }
            }
        }
        binding.btnCheckImage.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/"
            imagePicker.launch(intent)
        }
        binding.btnLogin.setOnClickListener {
            Log.d("minseok", "loginbtn")
            //로그인하기
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogout.setOnClickListener {
            //로그아웃하기
            fbAuth!!.signOut()
            updateUI(currentUser)
            val intent = intent
            startActivity(intent)
            finish()
        }
        binding.btnChangeInfo.setOnClickListener {
            val intent = Intent(this, ChangePwdActivity::class.java)
            startActivity(intent)
            finish()
        }

        if(currentUser!=null) {
            userRef!!.child(currentUser.uid).addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    val value = dataSnapshot.child("title").getValue(String::class.java)
                    if (value != null) {
                        adapter2!!.add(value)
                    }
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
        }
    }

    override fun onStart() {
        super.onStart()
        Log.i("액티비티 테스트", "onStart()")
        fbAuth = FirebaseAuth.getInstance()
        // 사용자가 로그인되어 있는지 확인
        val currentUser = fbAuth!!.currentUser
        updateUI(currentUser) // UI 업데이트
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: FirebaseUser?) {
        Log.i("액티비티 테스트", "updateUI")
        if (user != null) {
            Log.i("액티비티 테스트", user.email.toString())
        }
        if (user != null) {
            // 사용자가 로그인한 경우
            //val name=userRef!!.child(fbAuth!!.currentUser!!.uid).child("nickName").get().await().getValue(String::class.java)
            binding.tvName.text = "Hello ${user.email}"
            binding.btnLogin.visibility = View.GONE
            binding.btnLogout.visibility = View.VISIBLE
        } else {
            // 사용자가 로그아웃한 경우 또는 로그인하지 않은 경우
            binding.tvName.text = "로그인이 필요합니다."
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnLogout.visibility = View.GONE
            //binding.tvInfo.visibility = View.GONE
        }
    }
}