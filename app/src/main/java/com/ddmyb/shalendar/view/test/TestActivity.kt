package com.ddmyb.shalendar.view.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityTestBinding
import com.ddmyb.shalendar.view.home.navidrawer.NaviDrawerActivity
import com.ddmyb.shalendar.view.month.MonthActivity
import com.ddmyb.shalendar.view.month.MonthLibraryActivity
import com.ddmyb.shalendar.view.weekly.WeeklyCalendarActivity

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test)

        binding.monthCalendar.setOnClickListener {
            startActivity(
                Intent(this, MonthActivity::class.java)
            )
        }

        binding.dataBindingTestButton.setOnClickListener {
            val text = binding.dataBindingEditText.text.toString()
            binding.dataBindingTest = text
        }
        binding.btnWeek.setOnClickListener {
            val intent = Intent(this, WeeklyCalendarActivity::class.java)
            startActivity(intent)
        }

        binding.btnTestPage.setOnClickListener{
            val intent = Intent(this, NaviDrawerActivity::class.java)
            startActivity(intent)
        }

        binding.btnTestNaviDrawer.setOnClickListener{
//            val intent = Intent(this, TestNaviDrawer::class.java)
            startActivity(intent)
        }

        binding.timetableTest.setOnClickListener {
            val intent = Intent(this, TimeTableTestActivity::class.java)
            startActivity(intent)
        }

        binding.firebaseTest.setOnClickListener {
            val intent = Intent(this, FirebaseTestActivity::class.java)
            startActivity(intent)
        }

        binding.monthLibraryTest.setOnClickListener {
            val intent = Intent(this, MonthLibraryActivity::class.java)
            startActivity(intent)
        }

        binding.calendarProviderTest.setOnClickListener {
            val intent = Intent(this, CalendarProviderTestActivity::class.java)
            startActivity(intent)
        }
    }
}