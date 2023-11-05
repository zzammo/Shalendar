package com.ddmyb.shalendar.view.month

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthLibraryBinding
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarDateScheduleRVAdapter
import com.ddmyb.shalendar.view.month.presenter.MonthLibraryPresenter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class MonthLibraryFragment : Fragment(R.layout.fragment_month_library) {

    private lateinit var binding: FragmentMonthLibraryBinding
    private val logger = Logger("MonthCalendarPageFragment", true)
    private lateinit var dayOfWeekList: List<DayOfWeek>

    private val presenter = MonthLibraryPresenter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMonthLibraryBinding.bind(view)

        binding.calendarView.dayBinder = object :
            MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {

                val year = data.date.year
                val month = data.date.monthValue
                val day = data.date.dayOfMonth

                presenter.loadSchedule(year, month, day) {
                    if (container.view.isAttachedToWindow)
                        container.bind(data)
                }
                container.bind(data)

                if (container.scheduleListView.tag == null) {
                    container.scheduleListView.tag = data.date
                }
            }
        }

        binding.calendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, data: CalendarMonth) {

                logger.logD("month bind - ${data.yearMonth.year}, ${data.yearMonth.monthValue}")

                val year = data.yearMonth.year
                val month = data.yearMonth.monthValue

                presenter.loadHoliday(year, month) {
                    if (container.view.isAttachedToWindow)
                        binding.calendarView.notifyMonthChanged(data.yearMonth)
                }

                container.bind(data)

                if (container.dayOfWeekLayout.tag == null) {
                    // Remember that the header is reused so this will be called for each month.
                    // However, the first day of the week will not change so no need to bind
                    // the same view every time it is reused.
                    container.dayOfWeekLayout.tag = data.yearMonth
                    container.dayOfWeekBind()
                }
            }
        }

        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(100)  // Adjust as needed

        dayOfWeekList = daysOfWeek()  // Available from the library
        val firstDayOfWeek = dayOfWeekList.first()

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

//        presenter.loadData(startMonth, endMonth, currentMonth) {
//            binding.calendarView.notifyMonthChanged(it)
//        }
    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        val dayTextView = view.findViewById<TextView>(R.id.date)
        val lunarTextView = view.findViewById<TextView>(R.id.lunar_date)
        val scheduleListView = view.findViewById<RecyclerView>(R.id.schedules)

        // With ViewBinding
        // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

        fun bind(data: CalendarDay) {
            val year = data.date.year
            val month = data.date.monthValue
            val day = data.date.dayOfMonth

            dayTextView.text = day.toString()

            val lunarDate = presenter.toLunar(data.date)
            val lunarText = "${lunarDate.monthValue}/${lunarDate.dayOfMonth}"
            lunarTextView.text = lunarText

            if (presenter.scheduleList[LocalDate.of(year, month, day)] == null)
                presenter.scheduleList[LocalDate.of(year, month, day)] = MutableLiveListData()

            if (presenter.holidayList[LocalDate.of(year, month, day)] == null)
                presenter.holidayList[LocalDate.of(year, month, day)] = MutableLiveListData()

            val holidayList = presenter.holidayList[LocalDate.of(year, month, day)]!!.list
            val scheduleList = presenter.scheduleList[LocalDate.of(year, month, day)]!!.list
            val mutableList = MutableLiveListData<ScheduleDto>()

            scheduleListView.apply {
                layoutManager = object: LinearLayoutManager(context) {
                    override fun canScrollVertically(): Boolean {
                        return false
                    }
                }
                adapter = MonthCalendarDateScheduleRVAdapter(
                    mutableList
                )
            }

            val sList = presenter.holidayListToScheduleList(holidayList)
            for (schedule in sList) {
                mutableList.add(schedule)
                scheduleListView.adapter!!.notifyItemInserted(mutableList.list.size)
            }
            for (schedule in scheduleList) {
                mutableList.add(schedule)
                scheduleListView.adapter!!.notifyItemInserted(mutableList.list.size)
            }

            if (presenter.isSaturday(data.date))
                dayTextView.setTextColor(Color.BLUE)
            else if (presenter.isHoliday(data.date) || presenter.isSunday(data.date))
                dayTextView.setTextColor(Color.RED)

            if (data.position != DayPosition.MonthDate)
                this.view.alpha = 0.3f
        }

    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        // Alternatively, you can add an ID to the container layout and use findViewById()
        val monthTextView = view.findViewById<TextView>(R.id.month_text_view)
        val yearTextView = view.findViewById<TextView>(R.id.year_text_view)
        val dayOfWeekLayout = view.findViewById<LinearLayout>(R.id.day_of_week_layout)

        fun bind(data: CalendarMonth) {
            monthTextView.text = data.yearMonth.monthValue.toString()
            yearTextView.text = data.yearMonth.year.toString()
        }

        fun dayOfWeekBind() {
            dayOfWeekLayout.children.map { it as TextView }
                .forEachIndexed { index, textView ->
                    val dayOfWeek = dayOfWeekList[index]
                    val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    textView.text = title
                    // In the code above, we use the same `daysOfWeek` list
                    // that was created when we set up the calendar.
                    // However, we can also get the `daysOfWeek` list from the month data:
                    // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
                    // Alternatively, you can get the value for this specific index:
                    // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
                }
        }
    }
}