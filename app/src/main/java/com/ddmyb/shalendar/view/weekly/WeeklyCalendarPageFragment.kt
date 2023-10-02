package com.ddmyb.shalendar.view.weekly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.data.Schedule
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarPageBinding
import com.ddmyb.shalendar.view.weekly.data.WeeklyDates
import java.util.Calendar

class WeeklyCalendarPageFragment(private val now: Long): Fragment() {

    val TAG = "WeGlonD"
    private lateinit var binding: FragmentWeeklyCalendarPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeeklyCalendarPageBinding.inflate(inflater, container, false)
        val cal = Calendar.getInstance()
        cal.timeInMillis = now

        val weeklyDates = WeeklyDates(cal.get(Calendar.MONTH)+1, getWeekNums(cal))
        binding.data = weeklyDates

        displaySchedules(weeklyDates)

        return binding.root
    }

    fun getWeekNums(cal: Calendar): Array<Int> {
        val result = Array<Int>(7, {0})

        for (i in 0..6) {
            result[i] = cal.get(Calendar.DATE)
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        return result
    }

    fun displaySchedules(weeklyDates: WeeklyDates) {
        // 테스트용 코드
        val cal1 = Calendar.getInstance()
        cal1.set(Calendar.HOUR_OF_DAY, 2)
        cal1.set(Calendar.MINUTE, 18)
        val cal2 = Calendar.getInstance()
        cal2.set(Calendar.HOUR_OF_DAY, 4)
        cal2.set(Calendar.MINUTE, 25)

        displaySchedule(Schedule("test", cal1.timeInMillis, cal2.timeInMillis))
    }

    private fun displaySchedule(schedule: Schedule) {
        val startCal = Calendar.getInstance()
        startCal.timeInMillis = schedule.startTime
        val endCal = Calendar.getInstance()
        endCal.timeInMillis = schedule.endTime

        //이어서 할 것
    }
}