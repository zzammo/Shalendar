package com.ddmyb.shalendar.view.weekly

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.weekly.data.WeeklyDates
import com.ddmyb.shalendar.databinding.ActivityWeeklyCalendarBinding
import com.ddmyb.shalendar.view.weekly.adapter.WeeklyCalendarAdapter
import java.time.LocalDate
import java.util.Calendar

class WeeklyCalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeeklyCalendarBinding
    val TAG = "WeGlonD"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_weekly_calendar)

        binding.pager.adapter = WeeklyCalendarAdapter(getFirstDays(10),this)
        binding.pager.setCurrentItem(10, false)
    }

    fun getFirstDays(radius: Int): MutableList<Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        while (cal.get(Calendar.DAY_OF_WEEK) != 1) {
            cal.add(Calendar.DAY_OF_MONTH, -1)
        }

        val result = mutableListOf<Long>()
        cal.add(Calendar.DAY_OF_MONTH, -7*radius)
        for (i in 0..2*radius) {
            result.add(cal.timeInMillis)
            cal.add(Calendar.DAY_OF_MONTH, 7)
        }

        return result
    }
}