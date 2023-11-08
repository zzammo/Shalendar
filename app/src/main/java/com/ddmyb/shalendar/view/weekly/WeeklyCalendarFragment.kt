package com.ddmyb.shalendar.view.weekly

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.databinding.FragmentWeeklyCalendarBinding
import com.ddmyb.shalendar.view.home.CalendarFragment
import com.ddmyb.shalendar.view.weekly.adapter.WeeklyCalendarAdapter
import java.util.Calendar

class WeeklyCalendarFragment(private val startCal: Calendar, private val groupId: String?) : Fragment() {
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
        Log.d(TAG, "WeeklyCalendarFragment - onCreateView")
        Log.d(TAG, "startCal: ${startCal.get(Calendar.YEAR)}.${startCal.get(Calendar.MONTH)+1}.${startCal.get(Calendar.DATE)}")
        binding = FragmentWeeklyCalendarBinding.inflate(inflater)

        binding.pager.adapter = WeeklyCalendarAdapter(getFirstDays(100),groupId,requireActivity())
        convertPage(startCal)
//        binding.pager.setCurrentItem(100, false)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
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
            yearWeek2PageNum[cal.get(Calendar.YEAR)*100+cal.get(Calendar.WEEK_OF_YEAR)] = i
            cal.add(Calendar.DAY_OF_MONTH, 7)
        }

        return result
    }

    private fun convertPage(cal: Calendar) {
        val yearWeekNum = cal.get(Calendar.YEAR) * 100 + cal.get(Calendar.WEEK_OF_YEAR)
        val pageNum = yearWeek2PageNum[yearWeekNum]
        Log.d(TAG, "year-week: $yearWeekNum, page: $pageNum")
        if (pageNum != null) {
            binding.pager.setCurrentItem(pageNum, false)
            Log.d(TAG, "changed page: $pageNum")
        }
        else {
            val nowCal = Calendar.getInstance()
            val nowYearWeekNum = nowCal.get(Calendar.YEAR)*100+nowCal.get(Calendar.WEEK_OF_YEAR)
            val nowPageNum = yearWeek2PageNum[nowYearWeekNum]
            binding.pager.setCurrentItem(nowPageNum!!, false)
            Log.d(TAG, "changed page: $nowPageNum")
        }

    }
}