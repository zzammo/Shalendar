package com.ddmyb.shalendar.view.month

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarPageBinding
import com.ddmyb.shalendar.databinding.ItemMonthDateBinding
import com.ddmyb.shalendar.databinding.ItemMonthScheduleBinding
import com.ddmyb.shalendar.util.CalendarFunc
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.view.month.data.MonthCalendarDate
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
                itemBinding.root.alpha = 0.7F
            }

            (binding.dateLayout[i] as ViewGroup).addView(itemBinding.root)
        }

        return binding.root
    }

    private fun showScheduleDialog(date: MonthCalendarDate) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(getColor(requireContext(), R.color.transparent)))
        dialog.setContentView(R.layout.dialog_month_date_detail)

        dialog.findViewById<TextView>(R.id.date).text = date.date.toString()

        dialog.findViewById<TextView>(R.id.day_of_week).text =
            CalendarFunc.dayOfWeekOfDate(date.year, date.month, date.date)

        dialog.show()
    }

}