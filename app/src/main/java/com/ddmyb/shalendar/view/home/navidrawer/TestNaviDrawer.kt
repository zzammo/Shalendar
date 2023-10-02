package com.ddmyb.shalendar.view.home.navidrawer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.LoginActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.databinding.NaviDrawerTestPageBinding
import com.ddmyb.shalendar.view.home.navidrawer.adapter.OwnedCalendarAdapter
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class TestNaviDrawer : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    /*private var mFirebaseAuth: FirebaseAuth? = null*/
    private var mtvName: TextView? = null

    private val binding by lazy {
        NaviDrawerTestPageBinding.inflate(layoutInflater)
    }

    private lateinit var naviBinding: NaviDrawerBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[NaviViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.nvt_toolbar)
        setSupportActionBar(toolbar) // 툴바를 액티비티의 앱바로 지정

        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게


        naviBinding = NaviDrawerBinding.bind(binding.ndtpNavView.getHeaderView(0))
        binding.ndtpNavView.setNavigationItemSelectedListener(this) //navigation 리스너

        mtvName = naviBinding.tvName
        //mFirebaseAuth = FirebaseAuth.getInstance()
        //val currentUser = mFirebaseAuth!!.currentUser

        viewModel.loadAllCalendar{
            naviBinding.ndTeamcalendarRv.apply{
                adapter = OwnedCalendarAdapter(viewModel.getList())
                layoutManager = LinearLayoutManager(
                    this@TestNaviDrawer, LinearLayoutManager.VERTICAL, false)
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

        /*naviBinding.btnLogin.setOnClickListener{
            //로그인하기
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        naviBinding.btnLogout.setOnClickListener {
            //로그아웃하기
            mFirebaseAuth!!.signOut()
            updateUI(currentUser)
            val intent = intent
            startActivity(intent)
            finish()
        }*/
    }
    /*override fun onStart() {
        super.onStart()
        Log.i("액티비티 테스트", "onStart()")
        mFirebaseAuth = FirebaseAuth.getInstance()
        // 사용자가 로그인되어 있는지 확인
        val currentUser = mFirebaseAuth!!.currentUser
        updateUI(currentUser) // UI 업데이트
    }*/


    /*@SuppressLint("SetTextI18n")
    private fun updateUI(user: FirebaseUser?) {
        Log.i("액티비티 테스트", "updateUI")
        if (user != null) {
            // 사용자가 로그인한 경우
            naviBinding.tvName.text = "Hello ${user.email}!"
            naviBinding.btnLogin.visibility = View.GONE
            naviBinding.btnLogout.visibility = View.VISIBLE
        } else {
            // 사용자가 로그아웃한 경우 또는 로그인하지 않은 경우
            naviBinding.tvName.text = "로그인이 필요합니다."
            naviBinding.btnLogin.visibility = View.VISIBLE
            naviBinding.btnLogout.visibility = View.GONE
            //binding.tvInfo.visibility = View.GONE
        }
    }*/
    fun onClick(view: View) {
        var expandView: View? = null
        when (view) {
            naviBinding.ndUpDownIv -> {
                expandView = naviBinding.ndMycalendarLayout
            }
            naviBinding.ndUpDown2Iv -> {
                expandView = naviBinding.ndTeamcalendarRv
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_open_drawer, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                binding.ndtpDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_add_calendar-> Toast.makeText(this,"add clicked",Toast.LENGTH_SHORT).show()
        }
        return false
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() { //뒤로가기 처리
        if(binding.ndtpDrawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.ndtpDrawerLayout.closeDrawers()
            // 테스트를 위해 뒤로가기 버튼시 Toast 메시지
            Toast.makeText(this,"back btn clicked",Toast.LENGTH_SHORT).show()
        } else{
            super.onBackPressed()
        }
    }
}
