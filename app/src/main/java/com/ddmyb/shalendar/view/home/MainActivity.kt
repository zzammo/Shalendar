package com.ddmyb.shalendar.view.home

//import com.ddmyb.shalendar.view.dialog.TestDialog

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.view.login.LoginActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMainBinding
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.view.calendar_list.CalendarListFragment
import com.ddmyb.shalendar.view.alarm_manager.AlarmManagerFragment
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.dialog.CustomNewCalendarDialog
import com.ddmyb.shalendar.view.dialog.ParticipateTeamMateDialog
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.test.TestActivity
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.weather.WeatherTest

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userRepository = UserRepository.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        if (!userRepository!!.checkLogin()){
            Log.d("isLogin?","gogo")
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

        binding.amWeatherBtn.setOnClickListener {
            val intent = Intent(this, WeatherTest::class.java)
            startActivity(intent)
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frame, CalendarFragment(), "CalendarHostFragment").commit()

        binding.bottomNav.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.item_fragment1 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, CalendarFragment(), "CalendarHostFragment").commit()
                    binding.ivGroupAdd.visibility = View.GONE
                    binding.tvFragmentTitle.text = "개인 캘린더"
                    true
                }


                R.id.item_fragment2 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, CalendarListFragment()).commit()
                    binding.tvFragmentTitle.text = "그룹 관리"
                    binding.ivGroupAdd.visibility = View.VISIBLE
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
                    binding.tvFragmentTitle.text = "알람 관리"
                    binding.ivGroupAdd.visibility = View.GONE
                    true
                }

//                R.id.item_fragment5 -> {
//                    supportFragmentManager.beginTransaction()
//                        .replace(R.id.main_frame, ProflieFragment()).commit()
//                    binding.tvFragmentTitle.text = "내 정보"
//                    binding.ivGroupAdd.visibility = View.GONE
//                    binding.llCalendarOption.visibility = View.GONE
//
//                    true
//                }

                else -> false
            }
        }

        binding.ivGroupAdd.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_invite_or_participate)
            val inviteBtn : TextView = dialog.findViewById<TextView>(R.id.invite_teammate)
            val participateBtn : TextView = dialog.findViewById<TextView>(R.id.participate_group)
            inviteBtn.setOnClickListener {
                CustomNewCalendarDialog().show(this@MainActivity.supportFragmentManager, "")
                dialog.dismiss()
            }
            participateBtn.setOnClickListener{
                ParticipateTeamMateDialog().show(this@MainActivity.supportFragmentManager, "")
                dialog.dismiss()
            }
            dialog.show()

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