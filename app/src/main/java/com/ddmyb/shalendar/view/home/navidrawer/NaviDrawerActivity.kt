package com.ddmyb.shalendar.view.home.navidrawer

import ToggleAnimation
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.view.login.LoginActivity
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.domain.FBTest
import com.ddmyb.shalendar.domain.groups.repository.GroupRepository
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleRepository
import com.ddmyb.shalendar.view.home.navidrawer.adapter.OwnedCalendarAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NaviDrawerActivity :AppCompatActivity() {

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mDatabaseRef: DatabaseReference? = null //실시간 데이터베이스
    private var mtvName: TextView? = null
    private var adapter2: ArrayAdapter<String>? = null

    private var sclistView: ListView? = null

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
        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UsersSchedule")
        val currentUser = mFirebaseAuth!!.currentUser

        sclistView = binding.scheduleLV
        adapter2 = ArrayAdapter(this,android.R.layout.simple_list_item_1)
        sclistView!!.adapter = adapter2

        viewModel.loadAllCalendar {
            binding.ndTeamcalendarRv.apply {
                adapter = OwnedCalendarAdapter(viewModel.getList())
                layoutManager = LinearLayoutManager(
                    this@NaviDrawerActivity, LinearLayoutManager.VERTICAL, false
                )
                adapter!!.also { that ->
                    viewModel.ownedCalendarList.observeInsert {
                        that.notifyItemInserted(it)
                    }
                    viewModel.ownedCalendarList.observeRemove {
                        that.notifyItemRemoved(it)
                    }
                    viewModel.ownedCalendarList.observeChange {
                        that.notifyItemChanged(it)
                    }
                }
            }
        }
        binding.btnAddSc.setOnClickListener {
            val firebaseUser = mFirebaseAuth!!.currentUser
            if(firebaseUser!=null) {
                val mSc = ScheduleDto()
                ScheduleRepository().createUserSchedule(mSc)
                Toast.makeText(this@NaviDrawerActivity, "업로드 성공", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this@NaviDrawerActivity, "로그인 되어있지 않습니다", Toast.LENGTH_SHORT).show()
            }
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
        binding.btnLogin.setOnClickListener {
            Log.d("minseok", "loginbtn")
            //로그인하기
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.btnLogout.setOnClickListener {
            //로그아웃하기
            mFirebaseAuth!!.signOut()
            updateUI(currentUser)
            val intent = intent
            startActivity(intent)
            finish()
        }

        if(currentUser!=null) {
            mDatabaseRef!!.child(currentUser.uid).addChildEventListener(object : ChildEventListener {
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
        mFirebaseAuth = FirebaseAuth.getInstance()
        // 사용자가 로그인되어 있는지 확인
        val currentUser = mFirebaseAuth!!.currentUser
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

    fun onClick(view: View) {
        val expandView: View = binding.ndTeamcalendarRv
        if (expandView.visibility == View.VISIBLE) {
            ToggleAnimation.toggleArrow(view, true)
            ToggleAnimation.collapse(expandView)
        } else {
            ToggleAnimation.toggleArrow(view, false)
            ToggleAnimation.expand(expandView)
        }
    }
}