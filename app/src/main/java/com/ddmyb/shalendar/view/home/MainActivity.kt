package com.ddmyb.shalendar.view.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMainBinding
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.maptest.MapActivity
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.weather.WeatherTest
import com.ddmyb.shalendar.view.test.TestActivity

@RequiresApi(Build.VERSION_CODES.O)
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
//            binding.amWeatherBtn.setOnClickListener {
//                val intent = Intent(this, WeatherTest::class.java)
//                startActivity(intent)
//            }
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1 // 월은 0부터 시작
            HolidayApi.getHolidays(
                year = currentYear.toString(),
                month = currentMonth.toString(),
                object: HttpResult<List<HolidayDTO.HolidayItem>>{
                    override fun success(data: List<HolidayDTO.HolidayItem>) {
                        Log.d("minseok",data.toString())
                    }

                    override fun appFail() {
                        Log.d("minseok","appfail")
                    }

                    override fun fail(throwable: Throwable) {
                        Log.d("minseok","fail")
                    }

                    override fun finally() {
                        Log.d("minseok","finally")
                    }
                }
            )
    }
}