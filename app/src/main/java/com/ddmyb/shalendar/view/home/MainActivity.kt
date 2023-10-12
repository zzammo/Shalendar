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
import com.ddmyb.shalendar.view.holiday.api
import com.ddmyb.shalendar.view.maptest.MapActivity
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
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

            val namelist = ArrayList<String>()
            val datelist = ArrayList<Long>()

            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1 // 월은 0부터 시작

            api.getHolidays(
                year = currentYear.toString(),
                month = currentMonth.toString(),
                namelist = namelist,
                datelist = datelist
            )

            // API 응답을 처리
            // 여기에서 namelist 및 datelist를 사용하여 필요한 작업을 수행할 수 있습니다.
            /*for (i in datelist.indices) {
                Log.d("MainActivity", datelist[i].toString() + ' ' + namelist[i])
            }*/
    }
}