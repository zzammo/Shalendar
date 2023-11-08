package com.ddmyb.shalendar.view.home

import android.content.Intent
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
import com.ddmyb.shalendar.util.NewScheduleDto
import com.ddmyb.shalendar.view.month.MonthCalendarFragment
import com.ddmyb.shalendar.view.month.MonthLibraryDayClickListener
import com.ddmyb.shalendar.view.month.MonthLibraryFragment
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.weekly.WeeklyCalendarFragment
import com.ddmyb.shalendar.view.weekly.adapter.SlidingUpPanelAdapter
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import java.util.Calendar

class CalendarFragment(private val groupId: String? = null): Fragment() {
    private lateinit var binding: FragmentCalendarBinding
    var selectedDateCalendar: Calendar = Calendar.getInstance()
    private var fragmentNum = 0
    private val monthLibraryDayClickListener =
        object : MonthLibraryDayClickListener {
            override fun click(year: Int, month: Int, day: Int, scheduleList: MutableList<ScheduleDto>) {
                Log.d("CalendarFragment", "clicked $year/$month/$day\"")
                selectedDateCalendar.set(year, month-1, day)
            }

            override fun doubleClick(year: Int, month: Int, day: Int, scheduleList: MutableList<ScheduleDto>) {
                Log.d("CalendarFragment", "double clicked $year/$month/$day")
                selectedDateCalendar.set(year, month-1, 1)
                val cal = Calendar.getInstance()
                cal.set(year, month-1, day)
                openSlidingUpPanel(cal, ArrayList(scheduleList))
            }
        }
    private val fragments = arrayListOf(
        MonthLibraryFragment(
            selectedDateCalendar,
            groupId,
            monthLibraryDayClickListener),
        WeeklyCalendarFragment(selectedDateCalendar)
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCalendarBinding.inflate(inflater)


        var calendarFragmentPageAdapter = CalendarFragmentPageAdapter(fragments, requireActivity())
        binding.pager.apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = calendarFragmentPageAdapter
            isUserInputEnabled = false
            currentItem = 0
        }

        binding.swCalendarOption.setOnCheckedChangeListener { _, b ->
            if (b){
                fragments[1] = WeeklyCalendarFragment(selectedDateCalendar)
                calendarFragmentPageAdapter = CalendarFragmentPageAdapter(fragments,requireActivity())
                binding.pager.adapter = calendarFragmentPageAdapter
                binding.pager.currentItem = 1
                fragmentNum = 1
                Log.d("CalendarFragment", "$selectedDateCalendar")
            }
            else{
                fragments[0] = MonthLibraryFragment(
                    selectedDateCalendar,
                    groupId,
                    monthLibraryDayClickListener)
                binding.pager.adapter!!.notifyItemChanged(0)
                binding.pager.currentItem = 0
                fragmentNum = 0
                Log.d("CalendarFragment", "$selectedDateCalendar")
            }
        }

        return binding.root
    }

    fun openSlidingUpPanel(cal:Calendar, scheduleList: ArrayList<ScheduleDto>) {
        binding.tvToday.text = getDateString(cal)
        val slidingUpPanelAdapter = SlidingUpPanelAdapter(scheduleList, cal, requireContext())
        binding.planRecyclerView.adapter = slidingUpPanelAdapter
        slidingUpPanelAdapter.notifyDataSetChanged()
        binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), ScheduleActivity::class.java)
            intent.putExtra("NewSchedule", NewScheduleDto("", cal.timeInMillis))
            startActivity(intent)
            binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
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