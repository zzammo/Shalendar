package com.ddmyb.shalendar.view.home.navidrawer

import ToggleAnimation
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.LoginActivity
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.view.home.navidrawer.adapter.OwnedCalendarAdapter
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ddmyb.shalendar.domain.Schedule
import com.ddmyb.shalendar.domain.protoSchedule
import java.time.LocalDateTime

class NaviDrawerActivity :AppCompatActivity() {

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mDatabaseRef: DatabaseReference? = null //실시간 데이터베이스
    private var mtvName: TextView? = null
    private val mSc: Schedule? = null
    //private val mpSc: protoSchedule? = null

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
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users")
        val currentUser = mFirebaseAuth!!.currentUser

        viewModel.loadAllCalendar{
            binding.ndTeamcalendarRv.apply{
                adapter = OwnedCalendarAdapter(viewModel.getList())
                layoutManager = LinearLayoutManager(
                    this@NaviDrawerActivity, LinearLayoutManager.VERTICAL, false)
                adapter!!.also{that ->
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
            val mpSc=protoSchedule()
            //mpSc.apply { scheduleId="candy" }
            mDatabaseRef!!.child(firebaseUser!!.uid).push().setValue(mpSc)
            Toast.makeText(this@NaviDrawerActivity, "업로드 성공", Toast.LENGTH_SHORT).show()
        }

        binding.btnLogin.setOnClickListener{
            Log.d("minseok","loginbtn")
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
        if(expandView.visibility == View.VISIBLE) {
            ToggleAnimation.toggleArrow(view, true)
            ToggleAnimation.collapse(expandView)
        } else {
            ToggleAnimation.toggleArrow(view, false)
            ToggleAnimation.expand(expandView)
        }
    }

}