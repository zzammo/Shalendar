package com.ddmyb.shalendar.view.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarBinding
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarAdapter
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarFragmentAdapter
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarRecyclerViewAdapter
import java.util.Calendar

class MonthCalendarFragment(private val pageCount: Int) : Fragment() {

    private val logger = Logger("MonthCalendarFragment", true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMonthCalendarBinding =
            FragmentMonthCalendarBinding.inflate(inflater)

        val refList = makeRefs(pageCount)
        val fragmentList = makeFragments(refList)

        val monthCalendarAdapter: MonthCalendarAdapter = MonthCalendarFragmentAdapter(fragmentList, requireActivity())

        binding.pager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = (monthCalendarAdapter as RecyclerView.Adapter<*>)
            setCurrentItem(refList.size/2, false)
            offscreenPageLimit = 2
        }

        return binding.root
    }

    private fun makeRefs(count: Int): List<Long> {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, -count/2-1)
        for (i in -count/2..count/2) {
            cal.add(Calendar.MONTH, 1)
            logger.logD("$i : ${cal.get(Calendar.YEAR)}.${cal.get(Calendar.MONTH)+1}.${cal.get(Calendar.DATE)}")
            list.add(cal.timeInMillis)
        }
        return list
    }

    private fun makeFragments(refList: List<Long>): List<MonthCalendarPageFragment> {
        val fragmentList = mutableListOf<MonthCalendarPageFragment>()
        for (ref in refList) {
            fragmentList.add(MonthCalendarPageFragment(ref))
        }
        return fragmentList
    }

}