package com.ddmyb.shalendar.view.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMainBinding
import com.ddmyb.shalendar.databinding.ActivityMonthBinding
import com.ddmyb.shalendar.view.month.MonthActivity
import com.ddmyb.shalendar.view.weekly.WeeklyCalendarActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        binding.testButton.setOnClickListener {
            val intent = Intent(this, MonthActivity::class.java)
            startActivity(intent)
        }
        binding.btnWeek.setOnClickListener {
            val intent = Intent(this, WeeklyCalendarActivity::class.java)
            startActivity(intent)
        }
    }
}