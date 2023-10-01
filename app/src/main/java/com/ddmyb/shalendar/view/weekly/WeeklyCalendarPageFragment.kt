package com.ddmyb.shalendar.view.weekly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarPageBinding
import com.ddmyb.shalendar.view.weekly.data.WeeklyDates
import java.util.Calendar

class WeeklyCalendarPageFragment(private val now: Long): Fragment() {

    val TAG = "WeGlonD"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentWeeklyCalendarPageBinding.inflate(inflater, container, false)
        val cal = Calendar.getInstance()
        cal.timeInMillis = now

        val weeklyDates = WeeklyDates(cal.get(Calendar.MONTH)+1, getWeekNums(cal))
        binding.data = weeklyDates

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
}