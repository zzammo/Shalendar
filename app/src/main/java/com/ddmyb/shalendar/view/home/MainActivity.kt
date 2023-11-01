package com.ddmyb.shalendar.view.home

//import com.ddmyb.shalendar.view.dialog.TestDialog

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.FirebaseRepository
import com.ddmyb.shalendar.LoginActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMainBinding
import com.ddmyb.shalendar.dummy_fragment.CalendarListFragment
import com.ddmyb.shalendar.view.alarm_manager.AlarmManagerFragment
import com.ddmyb.shalendar.dummy_fragment.GroupCalendarFragment
import com.ddmyb.shalendar.dummy_fragment.PersonalCalendarFragment
import com.ddmyb.shalendar.dummy_fragment.ProflieFragment
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.maptest.MapActivity
import com.ddmyb.shalendar.view.month.MonthCalendarFragment
import com.ddmyb.shalendar.view.test.TestActivity
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.weather.WeatherTest

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val firebaseRepository = FirebaseRepository.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        if (!firebaseRepository!!.checkLogin()){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        binding.testButton.setOnClickListener {
            val intent = Intent(this, TestActivity::class.java)
            startActivity(intent)
        }

        binding.schedulesButton.setOnClickListener {
            val intent = Intent(this, ScheduleActivity::class.java)
            intent.putExtra("id", "dnaoidfnaodf")
            startActivity(intent)
        }

        binding.mapTestButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        binding.amWeatherBtn.setOnClickListener {
            val intent = Intent(this, WeatherTest::class.java)
            startActivity(intent)
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.main_frame, PersonalCalendarFragment()).commit()

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_fragment1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, MonthCalendarFragment(10)).commit()
                    true
                }


                R.id.item_fragment2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, CalendarListFragment()).commit()
                    true
                }

                R.id.item_fragment3 -> {
                    val intent = Intent(this, ScheduleActivity::class.java)
                    intent.putExtra("id", "dnaoidfnaodf")
                    startActivity(intent)
                    false
                }

                R.id.item_fragment4 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, AlarmManagerFragment()).commit()
                    true
                }

                R.id.item_fragment5 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, ProflieFragment()).commit()
                    true
                }

                else -> false
            }
        }
        HolidayApi.getHolidays(
            year = 2023,
            month = 12,
            object : HttpResult<List<HolidayDTO.HolidayItem>> {
                override fun success(data: List<HolidayDTO.HolidayItem>) {
                    Log.d("HolyDayApi", data.toString())
                }

                override fun appFail() {
                    Log.d("HolyDayApi", "appfail")
                }

                override fun fail(throwable: Throwable) {
                    Log.d("HolyDayApi", "fail")
                }

                override fun finally() {
                    Log.d("HolyDayApi", "finally")
                }
            }
        )
    }
}