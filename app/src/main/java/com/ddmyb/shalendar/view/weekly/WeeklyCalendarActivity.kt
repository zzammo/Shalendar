package com.ddmyb.shalendar.view.weekly

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.Schedule
import com.ddmyb.shalendar.databinding.ActivityWeeklyCalendarBinding
import com.ddmyb.shalendar.view.holiday.HolidayApiExplorer
import com.ddmyb.shalendar.view.weekly.adapter.SlidingUpPanelAdapter
import com.ddmyb.shalendar.view.weekly.adapter.WeeklyCalendarAdapter
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class WeeklyCalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeeklyCalendarBinding
    val TAG = "WeGlonD"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pager.adapter = WeeklyCalendarAdapter(getFirstDays(10),this)
        binding.pager.setCurrentItem(10, false)

        CoroutineScope(Dispatchers.IO).launch {
            val cal = Calendar.getInstance()
            val names = arrayListOf<String>()
            val dates = arrayListOf<Long>()
            HolidayApiExplorer.getHolidays(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, names, dates)
            Log.d(TAG, "holiday size: "+names.size)
            for (i in 0 until names.size) {
                val caltemp = Calendar.getInstance()
                cal.timeInMillis = dates[i]
                Log.d(TAG, "holiday " + names[i] + " " + caltemp.get(Calendar.YEAR) + caltemp.get(Calendar.MONTH)+1 + caltemp.get(Calendar.DATE))
            }
        }
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

    public fun openSlidingUpPanel(cal:Calendar, scheduleList: ArrayList<Schedule>) {
        binding.tvToday.text = getDateString(cal)
        val slidingUpPanelAdapter = SlidingUpPanelAdapter(scheduleList, cal, this@WeeklyCalendarActivity)
        binding.planRecyclerView.adapter = slidingUpPanelAdapter
        slidingUpPanelAdapter.notifyDataSetChanged()
        binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
    }

    private fun getDateString(cal: Calendar): String {
        val str = "${cal.get(Calendar.MONTH)}월 ${cal.get(Calendar.DATE)}일 "
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> return str + "일요일"
            Calendar.MONDAY -> return str + "월요일"
            Calendar.TUESDAY -> return str + "화요일"
            Calendar.WEDNESDAY -> return str + "수요일"
            Calendar.THURSDAY -> return str + "목요일"
            Calendar.FRIDAY -> return str + "금요일"
            Calendar.SATURDAY -> return str + "토요일"
            else -> return str
        }
    }
}