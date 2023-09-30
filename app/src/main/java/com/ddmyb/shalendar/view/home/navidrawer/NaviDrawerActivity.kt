package com.ddmyb.shalendar.view.home.navidrawer

import ToggleAnimation
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.view.home.navidrawer.adapter.OwnedCalendarAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class NaviDrawerActivity :AppCompatActivity() {

    private var mFirebaseAuth: FirebaseAuth? = null
    private var mtvName: TextView? = null


    private val binding by lazy {
        NaviDrawerBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[NaviViewModel::class.java]
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mtvName = binding.tvName
        mFirebaseAuth = FirebaseAuth.getInstance()
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
    }

    override fun onStart() {
        super.onStart()
        Log.i("액티비티 테스트", "onStart()")
        mFirebaseAuth = FirebaseAuth.getInstance()
        // 사용자가 로그인되어 있는지 확인
        val currentUser = mFirebaseAuth!!.currentUser
        updateUI(currentUser) // UI 업데이트
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // 사용자가 로그인한 경우
            binding.tvName!!.text = "환영합니다, " + user.email + "님!"
        } else {
            // 사용자가 로그아웃한 경우 또는 로그인하지 않은 경우
            binding.tvName!!.text = "로그인이 필요합니다."
            binding.button.isVisible= true
            binding.tvInfo.isVisible = false
        }
    }


    fun onClick(view: View) {
        var expandView: View? = null
        when (view) {
            binding.ndUpDownIv -> {
                expandView = binding.ndMycalendarLayout
            }
            binding.ndUpDown2Iv -> {
                expandView = binding.ndTeamcalendarRv
            }
        }
        if(expandView!!.visibility == View.VISIBLE) {
            ToggleAnimation.toggleArrow(view, true)
            ToggleAnimation.collapse(expandView)
        } else {
            ToggleAnimation.toggleArrow(view, false)
            ToggleAnimation.expand(expandView)
        }
    }

}