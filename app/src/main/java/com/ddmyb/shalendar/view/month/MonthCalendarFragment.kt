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
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarFragmentAdapter

class MonthCalendarFragment(private val refList: List<Long>) : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMonthCalendarBinding =
            FragmentMonthCalendarBinding.inflate(inflater)

        binding.pager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = MonthCalendarFragmentAdapter(refList, requireActivity())
            setCurrentItem(refList.size/2, false)
            offscreenPageLimit = 1
        }

        return binding.root
    }

}