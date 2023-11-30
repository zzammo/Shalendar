package com.ddmyb.shalendar.view.month

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentMonthLibraryBinding
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleRepository
import com.ddmyb.shalendar.domain.setting.repository.SettingRepository
import com.ddmyb.shalendar.domain.setting.repository.SettingRoom
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.home.CalendarFragment
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
import java.util.Calendar
import java.util.Locale

class MonthLibraryFragment(
    private val now: Calendar,
    private val groupId: String? = null,
    private val clickListener: MonthLibraryDayClickListener =
        object: MonthLibraryDayClickListener{
            override fun click(year: Int, month: Int, day: Int, groupId: String?, scheduleList: MutableList<ScheduleDto>) {

            }

            override fun doubleClick(year: Int, month: Int, day: Int, groupId: String?, scheduleList: MutableList<ScheduleDto>) {

            }
        }
): Fragment(R.layout.fragment_month_library) {

    private lateinit var binding: FragmentMonthLibraryBinding
    private val logger = Logger("MonthCalendarPageFragment", true)
    private lateinit var dayOfWeekList: List<DayOfWeek>
    private var lunarIndicate = true

    private val presenter = MonthLibraryPresenter(
        ScheduleRepository()
    )

    private var selectDate: LocalDate? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentMonthLibraryBinding.bind(view)

        lunarIndicate = SettingRepository.readSetting(requireContext()).lunar

        binding.calendarView.dayBinder = object :
            MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {

                if (data.date == selectDate) {
                    // If this is the selected date, show a round background and change the text color.
                    container.view.setBackgroundColor(requireContext().getColor(R.color.google_blue__33Alpha))
                } else {
                    // If this is NOT the selected date, remove the background and reset the text color.
                    container.view.setBackgroundColor(requireContext().getColor(R.color.white))
                }

                container.bind(data)

                if (data.position != DayPosition.MonthDate)
                    container.view.alpha = 0.3f
                else
                    container.view.alpha = 1.0f

                if (container.scheduleListView.tag == null) {
                    container.scheduleListView.tag = data.date
                }
            }
        }

        binding.calendarView.monthHeaderBinder = object :
            MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, data: CalendarMonth) {

                logger.logD("MonthViewContainer bind - ${data.yearMonth.year}, ${data.yearMonth.monthValue}")

                val year = data.yearMonth.year
                val month = data.yearMonth.monthValue

                if (presenter.holidayList[LocalDate.of(year, month, 1)] == null) {
                    logger.logD("MonthViewContainer loadHoliday ${data.yearMonth}")
                    presenter.loadHoliday(year, month) {
                        binding.calendarView.notifyMonthChanged(data.yearMonth)
                    }
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

        val currentMonth = YearMonth.of(now[Calendar.YEAR], now[Calendar.MONTH]+1)
        val startMonth = currentMonth.minusMonths(60)  // Adjust as needed
        val endMonth = currentMonth.plusMonths(60)  // Adjust as needed

        dayOfWeekList = daysOfWeek()  // Available from the library
        val firstDayOfWeek = dayOfWeekList.first()

        binding.calendarView.setup(startMonth, endMonth, firstDayOfWeek)
        binding.calendarView.scrollToMonth(currentMonth)

        binding.calendarView.monthScrollListener = {
            val year = it.yearMonth.year
            val month = it.yearMonth.monthValue-1
            val cal = Calendar.getInstance()
            cal.set(year, month, 1)
            (parentFragmentManager.findFragmentByTag("CalendarHostFragment") as CalendarFragment).selectedDateCalendar = cal
        }

//        presenter.loadData(startMonth, endMonth, currentMonth) {
//            binding.calendarView.notifyMonthChanged(it)
//        }
    }

    override fun onResume() {
        super.onResume()
        refreshSchedule()
    }

    private fun refreshSchedule() {
        logger.logD("refreshSchedule - occur")

        if (groupId == null)
            presenter.loadSchedule(afterEnd = {
                binding.calendarView.notifyCalendarChanged()
            })
        else
            presenter.loadGroupSchedule(groupId, afterEnd = {
                binding.calendarView.notifyCalendarChanged()
            })

        val settings =
            SettingRoom.getInstance(
                this@MonthLibraryFragment.requireContext()
            ).settingDao().getAll()

        if (settings.isNotEmpty() && settings[0].calendars != ""){
            for (id in settings[0].calendars.split('.')) {
                presenter.loadExternalSchedule(
                    requireActivity().contentResolver,
                    id.toInt(),
                    afterEnd = {
                        binding.calendarView.notifyCalendarChanged()
                    }
                )
            }
        }

    }

    inner class DayViewContainer(view: View) : ViewContainer(view) {
        private val dayTextView: TextView = view.findViewById(R.id.date)
        private val lunarTextView: TextView = view.findViewById(R.id.lunar_date)
        val scheduleListView: RecyclerView = view.findViewById(R.id.schedules)

        // With ViewBinding
        // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

        fun bind(data: CalendarDay) {
//            logger.logD("DayViewContainer bind")

            val year = data.date.year
            val month = data.date.monthValue
            val day = data.date.dayOfMonth

            dayTextView.text = day.toString()

            val lunar = presenter.toLunar(data.date)
            val lunarText = "${lunar.substring(4, 6)}/${lunar.substring(6, 8)}"
            lunarTextView.text = lunarText
            if (!lunarIndicate)
                lunarTextView.visibility = View.GONE

            setAdapter(year, month, day)

            if (presenter.isSaturday(data.date))
                dayTextView.setTextColor(Color.BLUE)
            if (presenter.isHoliday(data.date) || presenter.isSunday(data.date))
                dayTextView.setTextColor(Color.RED)

            view.setOnClickListener {
                val currentSelection = selectDate
                if (currentSelection == data.date) {
                    selectDate = null
                    clickListener.doubleClick(
                        year,
                        month,
                        day,
                        this@MonthLibraryFragment.groupId,
                        (scheduleListView.adapter!! as
                            MonthCalendarDateScheduleRVAdapter).scheduleList.list
                    )
                    binding.calendarView.notifyDateChanged(currentSelection)
                }
                else {
                    selectDate = data.date
                    binding.calendarView.notifyDateChanged(data.date)
                    clickListener.click(
                        year,
                        month,
                        day,
                        this@MonthLibraryFragment.groupId,
                        (scheduleListView.adapter!! as
                                MonthCalendarDateScheduleRVAdapter).scheduleList.list
                    )
                    if (currentSelection != null) {
                        binding.calendarView.notifyDateChanged(currentSelection)
                    }
                }
            }
        }

        private fun setAdapter(year: Int, month: Int, day: Int) {
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

            if (presenter.holidayList[LocalDate.of(year, month, day)] != null) {
                val holidayList = presenter.holidayList[LocalDate.of(year, month, day)]!!.list

                val sList = presenter.holidayListToScheduleList(holidayList)
                for (schedule in sList) {
                    logger.logD("setAdapter holiday ${schedule.title}")
                    mutableList.add(schedule)
                    scheduleListView.adapter!!.notifyItemInserted(mutableList.list.size)
                }
            }

            if (presenter.scheduleList[LocalDate.of(year, month, day)] != null) {
                val scheduleList = presenter.scheduleList[LocalDate.of(year, month, day)]!!.list

                for (schedule in scheduleList) {
                    mutableList.add(schedule)
                    scheduleListView.adapter!!.notifyItemInserted(mutableList.list.size)
                }
            }

            if (presenter.externalScheduleList[LocalDate.of(year, month, day)] != null) {
                val scheduleList = presenter.externalScheduleList[LocalDate.of(year, month, day)]!!.list

                for (schedule in scheduleList) {
                    mutableList.add(schedule)
                    scheduleListView.adapter!!.notifyItemInserted(mutableList.list.size)
                }
            }

        }

    }

    inner class MonthViewContainer(view: View) : ViewContainer(view) {
        // Alternatively, you can add an ID to the container layout and use findViewById()
        private val monthTextView: TextView = view.findViewById(R.id.month_text_view)
        private val yearTextView: TextView = view.findViewById(R.id.year_text_view)
        val dayOfWeekLayout: LinearLayout = view.findViewById(R.id.day_of_week_layout)

        fun bind(data: CalendarMonth) {
            logger.logD("MonthViewContainer bind")
            monthTextView.text = data.yearMonth.monthValue.toString()
            yearTextView.text = data.yearMonth.year.toString()
        }

        fun dayOfWeekBind() {
            logger.logD("MonthViewContainer dayOfWeekBind")
            dayOfWeekLayout.children.map { it as TextView }
                .forEachIndexed { index, textView ->
                    val dayOfWeek = dayOfWeekList[index]
                    val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    textView.text = title
                    if (index == 0)
                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    else if(index == 6)
                        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
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