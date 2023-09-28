package com.ddmyb.shalendar.view.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.CalendarDate
import com.ddmyb.shalendar.databinding.FragmentMonthCalendarPageBinding
import com.ddmyb.shalendar.databinding.ItemMonthDateBinding
import java.util.Calendar

class MonthCalendarPageFragment(private val now: Long) : Fragment() {

    var year: Int = 0
    var month: Int = 0
    private val calendarDateList: MutableList<CalendarDate> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cal = Calendar.getInstance()
        cal.timeInMillis = now
        cal.set(Calendar.DATE, 1)

        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)+1

        val dayOfWeek: Int =
            cal.get(Calendar.DAY_OF_WEEK) - 1 //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
        val max: Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월에 마지막 요일
        cal.add(Calendar.DATE, -dayOfWeek)

        for (j in 0 until dayOfWeek) {
            calendarDateList.add(
                CalendarDate(
                    year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    listOf())
            )
            cal.add(Calendar.DATE, 1)
        }
        for (j in 1..max) {
            calendarDateList.add(
                CalendarDate(
                    year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    listOf())
            )
            cal.add(Calendar.DATE, 1)
        }
        while (calendarDateList.size < 6*7) {
            calendarDateList.add(
                CalendarDate(
                    year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    listOf())
            )
            cal.add(Calendar.DATE, 1)
        }
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

        binding.fragment = this

        for (date in calendarDateList) {
            val itemBinding: ItemMonthDateBinding =
                DataBindingUtil.inflate(
                    inflater,
                    R.layout.item_month_date,
                    binding.dateLayout,
                    false)
            itemBinding.data = date
            //TODO: load schedules

            binding.dateLayout.addView(
                itemBinding.root
            )
        }

        return binding.root
    }



}