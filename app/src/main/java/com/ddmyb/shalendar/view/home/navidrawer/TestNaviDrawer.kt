package com.ddmyb.shalendar.view.home.navidrawer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.NaviDrawerTestPageBinding

class TestNaviDrawer : AppCompatActivity() {
    private val binding by lazy {
        NaviDrawerTestPageBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.nvt_toolbar)
        val drawerLayout = findViewById<DrawerLayout>(R.id.main_navigationView)

        setSupportActionBar(toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        toolbar.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                android.R.id.home ->
                    drawerLayout.openDrawer(GravityCompat.START) // 네비게이션 드로어 열기
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }
}
