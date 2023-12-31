package com.ddmyb.shalendar.view.home

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentCalendarBinding
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.util.CalendarProvider
import com.ddmyb.shalendar.util.NewScheduleDto
import com.ddmyb.shalendar.view.calendar_list.presenter.MyViewModel
import com.ddmyb.shalendar.view.dialog.CustomNewCalendarDialog
import com.ddmyb.shalendar.view.dialog.InviteDialog
import com.ddmyb.shalendar.view.dialog.ParticipateTeamMateDialog
import com.ddmyb.shalendar.view.lunar.LunarCalendar
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
    private lateinit var myViewModel: MyViewModel
    var selectedDateCalendar: Calendar = Calendar.getInstance()
    private var fragmentNum = 0
    private val monthLibraryDayClickListener =
        object : MonthLibraryDayClickListener {
            override fun click(year: Int, month: Int, day: Int, groupId: String?, scheduleList: MutableList<ScheduleDto>) {
                Log.d("CalendarFragment", "clicked $year/$month/$day")
                selectedDateCalendar.set(year, month-1, day)
            }

            override fun doubleClick(year: Int, month: Int, day: Int, groupId: String?, scheduleList: MutableList<ScheduleDto>) {
                Log.d("CalendarFragment", "double clicked $year/$month/$day")
                selectedDateCalendar.set(year, month-1, 1)
                val cal = Calendar.getInstance()
                cal.set(year, month-1, day)
                val showScheduleList = mutableListOf<ScheduleDto>()
                for (schedule in scheduleList) {
                    if ((schedule.userId != UserRepository.getInstance()!!.getUserId() &&
                                schedule.groupId == "") ||
                        schedule.groupId != (groupId ?: "")
                    ) continue
                    showScheduleList.add(schedule)
                }
                openSlidingUpPanel(cal, ArrayList(showScheduleList))
            }
        }
    private val fragments = arrayListOf(
        MonthLibraryFragment(
            selectedDateCalendar,
            groupId,
            monthLibraryDayClickListener),
        WeeklyCalendarFragment(selectedDateCalendar, groupId)
    )

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
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

        myViewModel = ViewModelProvider(requireActivity())[MyViewModel::class.java]
        myViewModel.groupId = groupId

        binding.swCalendarOption.setOnCheckedChangeListener { _, b ->
            if (b){
                fragments[1] = WeeklyCalendarFragment(selectedDateCalendar, groupId)
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

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result == true) {
                    Log.d("CalendarProviderTestActivity", "permission true")
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please allow this app to access your calendar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)

        return binding.root
    }

    fun openSlidingUpPanel(cal:Calendar, scheduleList: ArrayList<ScheduleDto>) {
        binding.tvToday.text = getDateString(cal)
        val simpleLunarString = LunarCalendar.SolarToLunar(getSimpleDateString(cal))
        Log.d("WeGlonD", simpleLunarString)
        binding.tvLunar.text = "음력 ${simpleLunarString.substring(4,6).toInt()}월 ${simpleLunarString.substring(6).toInt()}일"
        val slidingUpPanelAdapter = SlidingUpPanelAdapter(scheduleList, cal, requireContext())
        binding.planRecyclerView.adapter = slidingUpPanelAdapter
        slidingUpPanelAdapter.notifyDataSetChanged()
        binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.ANCHORED
        binding.btnAdd.setOnClickListener {
            val intent = Intent(requireContext(), ScheduleActivity::class.java)
            val gID: String = when(groupId) {
                null -> ""
                else -> groupId
            }
            intent.putExtra("NewSchedule", NewScheduleDto("", cal.timeInMillis, gID))
            startActivity(intent)
            binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("WeGlonD", "CalendarFragment onResume")
        when(binding.slidingMainFrame.panelState) {
            SlidingUpPanelLayout.PanelState.ANCHORED -> {
                Log.d("WeGlonD", "SlidingUpPanel ANCHORED")
                binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
            SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                Log.d("WeGlonD", "SlidingUpPanel COLLAPSED")
                binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
            SlidingUpPanelLayout.PanelState.DRAGGING -> {
                Log.d("WeGlonD", "SlidingUpPanel DRAGGING")
                binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
            SlidingUpPanelLayout.PanelState.EXPANDED -> {
                Log.d("WeGlonD", "SlidingUpPanel EXPANDED")
                binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
            SlidingUpPanelLayout.PanelState.HIDDEN -> {
                Log.d("WeGlonD", "SlidingUpPanel HIDDEN")
                binding.slidingMainFrame.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
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

    private fun getSimpleDateString(cal: Calendar): String {
        return "${cal.get(Calendar.YEAR)}${String.format("%02d", cal.get(Calendar.MONTH)+1)}${String.format("%02d", cal.get(Calendar.DATE))}"
    }
}