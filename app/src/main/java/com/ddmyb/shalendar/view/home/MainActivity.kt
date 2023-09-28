package com.ddmyb.shalendar.view.home

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.weekly.WeeklyCalendarActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_week).setOnClickListener {
            val intent = Intent(this, WeeklyCalendarActivity::class.java)
            startActivity(intent)
        }
    }
}