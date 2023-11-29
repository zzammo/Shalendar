package com.ddmyb.shalendar.view.home

//import com.ddmyb.shalendar.view.dialog.TestDialog

import android.app.AlarmManager
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.ddmyb.shalendar.view.login.LoginActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMainBinding
import com.ddmyb.shalendar.domain.groups.repository.GroupRepository
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.view.calendar_list.CalendarListFragment
import com.ddmyb.shalendar.view.alarm_manager.AlarmManagerFragment
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.calendar_list.presenter.MyViewModel
import com.ddmyb.shalendar.view.dialog.CustomNewCalendarDialog
import com.ddmyb.shalendar.view.dialog.DialogListener
import com.ddmyb.shalendar.view.dialog.InviteDialog
import com.ddmyb.shalendar.view.dialog.ParticipateTeamMateDialog
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.test.TestActivity
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.setting.SettingFragment
import com.ddmyb.shalendar.view.schedules.utils.Permission
import com.ddmyb.shalendar.view.weather.WeatherTest

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), DialogListener {
    private lateinit var binding: ActivityMainBinding
    private val userRepository = UserRepository.getInstance()
    private lateinit var myViewModel: MyViewModel
    private lateinit var calendarListFragment: CalendarListFragment
    private val permissions = arrayOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.READ_CALENDAR,
        android.Manifest.permission.WRITE_CALENDAR
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        // ViewModelProvider를 통해 ViewModel 인스턴스를 얻음
        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]

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
                    calendarListFragment = CalendarListFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, calendarListFragment).commit()
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

                R.id.item_fragment5 -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, SettingFragment()).commit()
                    binding.tvFragmentTitle.text = "내 정보"
                    binding.ivGroupAdd.visibility = View.GONE
                    true
                }

                else -> false
            }
        }

        binding.ivGroupAdd.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.dialog_invite_or_participate)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            if(binding.tvFragmentTitle.text.equals("그룹 관리")){
                Log.d("oz","MainActivity 그룹관리 Dialog")
                val inviteBtn : TextView = dialog.findViewById<TextView>(R.id.invite_teammate)
                val participateBtn : TextView = dialog.findViewById<TextView>(R.id.participate_group)
                inviteBtn.setOnClickListener {
                    CustomNewCalendarDialog().show(this@MainActivity.supportFragmentManager, "")
                    dialog.dismiss()
                }
                participateBtn.setOnClickListener{
                    val participateDialog = ParticipateTeamMateDialog()
                    participateDialog.setDialogListener(this)
                    participateDialog.show(this@MainActivity.supportFragmentManager, "")
                    dialog.dismiss()
                }
                dialog.show()
            }
            else{
                Log.d("oz","MainActivity not 그룹관리 Dialog")
                dialog.setContentView(R.layout.dialog_invite_code)
                val inviteDialog = InviteDialog(myViewModel.groupId!!)
                inviteDialog.show(supportFragmentManager, "invite_dialog_tag")
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
        requestInitialPermissions()
    }

    override fun onResume() {
        super.onResume()
        //requestInitialPermissions()
    }

    fun requestInitialPermissions() {
        drawOverOtherApps()
        scheduleExactAlarm()
        ignoreBatteryOptimization()
        requestPermissions(permissions, Permission.PERMISSIONS_REQUEST_CODE)
    }

    fun drawOverOtherApps() {
        if (!Settings.canDrawOverlays(this)) {
            Log.d("WeGlonD", "Permission REQ: SYSTEM_ALERT_WINDOW")
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName))
            startActivity(intent)
        }
    }

    fun scheduleExactAlarm() {
        val alarmManager = getSystemService<AlarmManager>()!!
        if(VERSION.SDK_INT >= VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.d("WeGlonD", "Permission REQ: SCHEDULE_EXACT_ALARM")
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:" + packageName))
                startActivity(intent)
            }
        }
    }

    fun ignoreBatteryOptimization() {
        val pm = getSystemService(POWER_SERVICE) as PowerManager
        if(!pm.isIgnoringBatteryOptimizations(packageName)) {
            Log.d("WeGlonD", "Permission REQ: REQUEST_IGNORE_BATTERY_OPTIMIZATIONS")
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + packageName))
            startActivity(intent)
        }
    }

    override fun onDialogClosed(message: String?) {
        calendarListFragment.getGroup()
    }
}