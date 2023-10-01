package com.ddmyb.shalendar.view.month.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ddmyb.shalendar.view.month.MonthCalendarPageFragment

class MonthCalendarFragmentAdapter(
    private val refList: List<Long>,
    fragmentActivity: FragmentActivity
): FragmentStateAdapter(fragmentActivity), MonthCalendarAdapter {

    override fun createFragment(position: Int): Fragment {
        return MonthCalendarPageFragment(refList[position])
    }

    override fun getItemCount(): Int {
        return refList.size
    }
}