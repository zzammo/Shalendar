package com.ddmyb.shalendar.view.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ddmyb.shalendar.databinding.FragmentCalendarBinding
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.view.month.MonthCalendarFragment
import com.ddmyb.shalendar.view.month.MonthLibraryDayClickListener
import com.ddmyb.shalendar.view.month.MonthLibraryFragment
import com.ddmyb.shalendar.view.weekly.WeeklyCalendarFragment
import com.ddmyb.shalendar.view.weekly.adapter.SlidingUpPanelAdapter
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.util.Calendar

class CalendarFragment(private val groupId: String? = null): Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    var selectedDateCalendar: Calendar = Calendar.getInstance()
    private var fragmentNum = 0
    private val fragments = arrayListOf(
        MonthLibraryFragment(
            selectedDateCalendar,
            groupId,
            object : MonthLibraryDayClickListener {
                override fun click(year: Int, month: Int, day: Int, scheduleList: MutableList<ScheduleDto>) {
                    Toast.makeText(requireContext(), "clicked $year/$month/$day", Toast.LENGTH_SHORT).show()
                    Log.d("CalendarFragment", scheduleList.toString())
                    selectedDateCalendar.set(year, month-1, day)
                }

                override fun doubleClick(year: Int, month: Int, day: Int, scheduleList: MutableList<ScheduleDto>) {
                    Toast.makeText(requireContext(), "double clicked $year/$month/$day", Toast.LENGTH_SHORT).show()
                    Log.d("CalendarFragment", scheduleList.toString())
                    selectedDateCalendar.set(year, month-1, day)
                }
            }),
        WeeklyCalendarFragment(selectedDateCalendar)
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater)

        binding.btnSelectMonth.setOnClickListener {
            if (fragmentNum != 0) {
                fragments[0] = MonthLibraryFragment(
                    selectedDateCalendar,
                    groupId,
                    object : MonthLibraryDayClickListener {
                        override fun click(year: Int, month: Int, day: Int, scheduleList: MutableList<ScheduleDto>) {
                            Toast.makeText(requireContext(), "clicked $year/$month/$day", Toast.LENGTH_SHORT).show()
                            Log.d("CalendarFragment", scheduleList.toString())
                            selectedDateCalendar.set(year, month-1, day)
                        }

                        override fun doubleClick(year: Int, month: Int, day: Int, scheduleList: MutableList<ScheduleDto>) {
                            Toast.makeText(requireContext(), "double clicked $year/$month/$day", Toast.LENGTH_SHORT).show()
                            Log.d("CalendarFragment", scheduleList.toString())
                            selectedDateCalendar.set(year, month-1, day)
                        }
                    })
                binding.pager.currentItem = 0
                fragmentNum = 0
                Log.d("CalendarFragment", "$selectedDateCalendar")
            }
        }

        var calendarFragmentPageAdapter = CalendarFragmentPageAdapter(fragments, requireActivity())
        binding.pager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = calendarFragmentPageAdapter
            isUserInputEnabled = false
            currentItem = 0
        }

        binding.btnSelectWeek.setOnClickListener {
            if (fragmentNum != 1) {
                fragments[1] = WeeklyCalendarFragment(selectedDateCalendar)
                calendarFragmentPageAdapter = CalendarFragmentPageAdapter(fragments, requireActivity())
                binding.pager.adapter = calendarFragmentPageAdapter
                binding.pager.currentItem = 1
                fragmentNum = 1
                Log.d("CalendarFragment", "$selectedDateCalendar")
            }
        }

        return binding.root
    }

    public fun openSlidingUpPanel(cal:Calendar, scheduleList: ArrayList<ScheduleDto>) {
        binding.tvToday.text = getDateString(cal)
        val slidingUpPanelAdapter = SlidingUpPanelAdapter(scheduleList, cal, requireContext())
        binding.planRecyclerView.adapter = slidingUpPanelAdapter
        slidingUpPanelAdapter.notifyDataSetChanged()
        binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
    }

    private fun getDateString(cal: Calendar): String {
        val str = "${cal.get(Calendar.MONTH)+1}월 ${cal.get(Calendar.DATE)}일 "
        when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> return str + "일요일"
            Calendar.MONDAY -> return str + "월요일"
            Calendar.TUESDAY -> return str + "화요일"
            Calendar.WEDNESDAY -> return str + "수요일"
            Calendar.THURSDAY -> return str + "목요일"
            Calendar.FRIDAY -> return str + "금요일"
            Calendar.SATURDAY -> return str + "토요일"
            else -> return str
        }
    }
}