package com.ddmyb.shalendar.view.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarBinding
import com.ddmyb.shalendar.view.month.adapter.CalendarAdapter

class MonthCalendarFragment(private val refList: List<Long>) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMonthCalendarBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_month_calendar,
                container,
                false
            )

        binding.pager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = CalendarAdapter(refList, requireActivity())
        }

        binding.pager.setCurrentItem(refList.size/2, false)
        binding.pager.offscreenPageLimit = 2

        return binding.root
    }

}