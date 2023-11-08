package com.ddmyb.shalendar.view.weekly.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddmyb.shalendar.view.weekly.WeeklyCalendarPageFragment

class WeeklyCalendarAdapter(
    private val firstDays: List<Long>,
    private val groupId: String?,
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return firstDays.size
    }

    override fun createFragment(position: Int): Fragment {
        return WeeklyCalendarPageFragment(firstDays[position], groupId)
    }
}