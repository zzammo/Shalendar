package com.ddmyb.shalendar.view.month

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarPageBinding
import com.ddmyb.shalendar.databinding.ItemMonthDateBinding
import com.ddmyb.shalendar.databinding.ItemMonthScheduleBinding
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.view.month.presenter.MonthCalendarPagePresenter
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
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH)+1,
            mutableListOf()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentMonthCalendarPageBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_month_calendar_page,
                container,
                false)

        binding.presenter = presenter

        for (i in 0 until presenter.monthCalendarDateList.size) {
            val itemBinding: ItemMonthDateBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_month_date,
                    binding.dateLayout[i] as ViewGroup,
                    false)

            val calendarDate = presenter.monthCalendarDateList[i]

            calendarDate.scheduleList.apply {
                this.observeChange {
                    itemBinding.schedules.removeViewAt(it)
                    itemBinding.schedules.addView(
                        DataBindingUtil.inflate<ItemMonthScheduleBinding>(
                            inflater,
                            R.layout.item_month_schedule,
                            itemBinding.schedules,
                            false
                        ).root,
                        it
                    )
                }
                this.observeInsert {
                    itemBinding.schedules.addView(
                        DataBindingUtil.inflate<ItemMonthScheduleBinding>(
                            inflater,
                            R.layout.item_month_schedule,
                            itemBinding.schedules,
                            false
                        ).root,
                        it
                    )
                }
                this.observeRemove {
                    itemBinding.schedules.removeViewAt(it)
                }
            }
            presenter.loadSchedule(i)
            //TODO: load schedules

            itemBinding.root.setOnClickListener {
                if (!presenter.selectDate(calendarDate)) {
                    //TODO: pop up
                }
                else {
                    //TODO: draw selected line
                }
            }

            itemBinding.data = calendarDate

            if (presenter.isSaturday(calendarDate)) {
                itemBinding.date.setTextColor(Color.BLUE)
            }
            else if (presenter.isSunday(calendarDate) || presenter.isHoliday(calendarDate)) {
                itemBinding.date.setTextColor(Color.RED)
            }

            if (!presenter.isThisMonth(calendarDate)) {
                itemBinding.root.alpha = 0.7F
            }

            (binding.dateLayout[i] as ViewGroup).addView(itemBinding.root)
        }

        return binding.root
    }

}