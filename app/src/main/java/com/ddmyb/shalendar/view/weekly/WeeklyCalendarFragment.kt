package com.ddmyb.shalendar.view.weekly

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarBinding
import com.ddmyb.shalendar.view.weekly.adapter.WeeklyCalendarAdapter
import java.util.Calendar

class WeeklyCalendarFragment : Fragment() {

    private lateinit var binding: FragmentWeeklyCalendarBinding
    val yearWeek2PageNum = HashMap<Int, Int>()
    private val TAG = "WeGlonD"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeeklyCalendarBinding.inflate(inflater)

        binding.pager.adapter = WeeklyCalendarAdapter(getFirstDays(100),requireActivity())
        binding.pager.setCurrentItem(100, false)

        return binding.root
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
            yearWeek2PageNum.put(cal.get(Calendar.YEAR)*100+cal.get(Calendar.MONTH)+1, i)
            cal.add(Calendar.DAY_OF_MONTH, 7)
        }

        return result
    }
}