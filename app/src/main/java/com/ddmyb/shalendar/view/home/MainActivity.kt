package com.ddmyb.shalendar.view.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMainBinding
//import com.ddmyb.shalendar.view.dialog.TestDialog
import com.ddmyb.shalendar.util.KakaoInvite
import com.ddmyb.shalendar.view.maptest.MapActivity
import com.ddmyb.shalendar.view.test.TestActivity
import com.ddmyb.shalendar.view.schedules.ScheduleActivity

    class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

            binding.testButton.setOnClickListener {
                val intent = Intent(this, TestActivity::class.java)
                startActivity(intent)
            }

            binding.schedulesButton.setOnClickListener {
                val intent = Intent(this, ScheduleActivity::class.java)
                intent.putExtra("id", "dnaoidfnaodf")
                startActivity(intent)
            }
//            binding.dialogTestButton.setOnClickListener {
//                val intent = Intent(this, TestDialog::class.java)
//                startActivity(intent)
//            }
            binding.mapTestButton.setOnClickListener {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            }
    }
}