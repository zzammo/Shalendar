package com.ddmyb.shalendar.view.month.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddmyb.shalendar.view.month.MonthCalendarPageFragment
import com.ddmyb.shalendar.view.month.TimeTableFragment
import com.ddmyb.shalendar.view.month.data.TimeTableScheduleList

class MonthDateDetailAdapter(
    fragmentActivity: FragmentActivity,
    private val timetable: TimeTableFragment
): FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return timetable
    }

    override fun getItemCount(): Int {
        return 1
    }
}