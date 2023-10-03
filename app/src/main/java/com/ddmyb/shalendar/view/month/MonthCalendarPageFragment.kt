package com.ddmyb.shalendar.view.month

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarPageBinding
import com.ddmyb.shalendar.databinding.ItemMonthDateBinding
import com.ddmyb.shalendar.databinding.ItemMonthScheduleBinding
import com.ddmyb.shalendar.util.CalendarFunc
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarDateScheduleRVAdapter
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarFragmentAdapter
import com.ddmyb.shalendar.view.month.adapter.MonthDateDetailAdapter
import com.ddmyb.shalendar.view.month.data.MonthCalendarDateData
import com.ddmyb.shalendar.view.month.data.MonthPageData
import com.ddmyb.shalendar.view.month.data.TimeTableScheduleList
import com.ddmyb.shalendar.view.month.presenter.MonthCalendarPagePresenter
import com.islandparadise14.mintable.MinTimeTableView
import com.islandparadise14.mintable.model.ScheduleDay
import com.islandparadise14.mintable.model.ScheduleEntity
import java.util.Calendar

class MonthCalendarPageFragment(private val now: Long) : Fragment() {

    private val logger = Logger("MonthCalendarPageFragment", true)

    private lateinit var presenter: MonthCalendarPagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cal = Calendar.getInstance()
        cal.timeInMillis = now

        logger.logD("${cal.get(Calendar.YEAR)}, ${cal.get(Calendar.MONTH)+1}")

        presenter = MonthCalendarPagePresenter(
            MonthPageData(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH)+1,
                mutableListOf()
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMonthCalendarPageBinding =
            FragmentMonthCalendarPageBinding.inflate(inflater)

        binding.data = presenter.pageData

        for (i in 0 until presenter.pageData.calendarDateList.size) {
            val itemBinding: ItemMonthDateBinding = DataBindingUtil.bind(binding.dateLayout[i])!!

            val calendarDate = presenter.pageData.calendarDateList[i]

            itemBinding.schedules.apply {
                layoutManager = object: LinearLayoutManager(requireContext()) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                adapter = MonthCalendarDateScheduleRVAdapter(calendarDate.scheduleList)

            }

            calendarDate.scheduleList.apply {
                this.observeChange {
                    itemBinding.schedules.adapter!!.notifyItemChanged(it)
                }
                this.observeInsert {
                    itemBinding.schedules.adapter!!.notifyItemInserted(it)
                }
                this.observeRemove {
                    itemBinding.schedules.adapter!!.notifyItemRemoved(it)
                }
            }
            presenter.loadSchedule(i)

            itemBinding.root.setOnClickListener {
                val selectedIdx = presenter.selectDate(i)

                logger.logD("$selectedIdx, $i")
                if (selectedIdx != -1)
                    binding.dateLayout[selectedIdx].background = null

                binding.dateLayout[i].background = AppCompatResources.getDrawable(requireContext(), R.drawable.month_date_selected)

                showScheduleDialog(calendarDate)
            }

            itemBinding.data = calendarDate

            if (presenter.isSaturday(calendarDate)) {
                itemBinding.date.setTextColor(Color.BLUE)
            }
            else if (presenter.isSunday(calendarDate) || presenter.isHoliday(calendarDate)) {
                itemBinding.date.setTextColor(Color.RED)
            }

            if (!presenter.isThisMonth(calendarDate)) {
                itemBinding.root.alpha = 0.3F
            }
        }

        return binding.root
    }

    private fun showScheduleDialog(date: MonthCalendarDateData) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(getColor(requireContext(), R.color.transparent)))
        dialog.setContentView(R.layout.dialog_month_date_detail)

        dialog.findViewById<TextView>(R.id.date).text = date.date.toString()

        dialog.findViewById<TextView>(R.id.day_of_week).text = presenter.getWeekOfDay(date)
//
//        dialog.findViewById<FrameLayout>(R.id.timetable).addView(
//            TimeTableFragment(listOf(TimeTableScheduleList(" ", date.scheduleList.list))).binding.root
//        )

        logger.logD("$parentFragment")
        logger.logD("$childFragmentManager")

//        requireActivity().supportFragmentManager.commit {
//            add(R.id.timetable,
//                TimeTableFragment(
//                    listOf(TimeTableScheduleList(" ", date.scheduleList.list))
//                )
//            )
//            setReorderingAllowed(true)
//        }

        dialog.findViewById<ViewPager2>(R.id.pager).apply {
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            adapter = MonthDateDetailAdapter(
                requireActivity(),
                TimeTableFragment(
                    listOf(
                        TimeTableScheduleList("n1", date.scheduleList.list),
                    ),
                    idxHeight = 50,
                    idxWidthPercentage = 0.5f
                )
            )
            setCurrentItem(0, false)
            offscreenPageLimit = 1
        }

        dialog.show()

//        val dialog = MonthDateDetailDialogFragment()

    }

}