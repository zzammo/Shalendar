package com.ddmyb.shalendar.view.month.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddmyb.shalendar.view.month.MonthCalendarPageFragment

class MonthCalendarFragmentAdapter(
    private val fragmentList: List<MonthCalendarPageFragment>,
    fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity), MonthCalendarAdapter {

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }
}