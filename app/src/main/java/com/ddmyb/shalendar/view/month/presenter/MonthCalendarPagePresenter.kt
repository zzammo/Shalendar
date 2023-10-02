package com.ddmyb.shalendar.view.month.presenter

import android.graphics.Color
import com.ddmyb.shalendar.util.CalendarFunc
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.data.MonthCalendarDateData
import com.ddmyb.shalendar.view.month.data.MonthPageData
import com.ddmyb.shalendar.view.month.data.ScheduleData
import java.util.Calendar

class MonthCalendarPagePresenter(
    val pageData: MonthPageData
) {

    private var selected: Int

    init {
        val cal = Calendar.getInstance()
        cal.set(pageData.year, pageData.month-1, 1)

        val dayOfWeek: Int =
            cal.get(Calendar.DAY_OF_WEEK) - 1 //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
        val max: Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월에 마지막 요일
        cal.add(Calendar.DATE, -dayOfWeek)

        for (j in 0 until dayOfWeek) {
            pageData.calendarDateList.add(
                MonthCalendarDateData(
                    pageData.year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }
        for (j in 1..max) {
            pageData.calendarDateList.add(
                MonthCalendarDateData(
                    pageData.year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }
        while (pageData.calendarDateList.size < 6*7) {
            pageData.calendarDateList.add(
                MonthCalendarDateData(
                    pageData.year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }

        selected = -1
    }

    fun loadSchedule(idx: Int) {
        //TODO: load schedules
        pageData.calendarDateList[idx].scheduleList.add(
            ScheduleData(
                "n1",
                (1000 * 60 * 60) * 3L,
                (1000 * 60 * 60) * 4L,
                Color.BLUE
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            ScheduleData(
                "n2",
                (1000 * 60 * 60) * 5L,
                (1000 * 60 * 60) * 10L,
                Color.RED
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            ScheduleData(
                "n3",
                (1000 * 60 * 60) * 12L,
                (1000 * 60 * 60) * 14L,
                Color.CYAN
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            ScheduleData(
                "n4",
                (1000 * 60 * 60) * 15L,
                (1000 * 60 * 60) * 19L,
                Color.GRAY
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            ScheduleData(
                "n5",
                (1000 * 60 * 60) * 20L,
                ((1000 * 60 * 60) * 20.5).toLong(),
                Color.YELLOW
            )
        )
    }

    fun selectDate(idx: Int): Int {
        val preIdx = selected
        selected = idx
        return preIdx
    }

    fun getWeekOfDay(date: MonthCalendarDateData): String {
        val cal = Calendar.getInstance()
        cal.set(date.year, date.month, date.date)
        return CalendarFunc.dayOfWeekOfDate(cal.timeInMillis)
    }

    fun getHMs(schedule: ScheduleData): Pair<String, String> {
        return Pair(
            CalendarFunc.extractHM(schedule.startTime),
            CalendarFunc.extractHM(schedule.endTime)
        )
    }

    fun isSaturday(date: MonthCalendarDateData): Boolean {
        val cal = Calendar.getInstance()
        cal.set(date.year, date.month-1, date.date)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
    }
    fun isSunday(date: MonthCalendarDateData): Boolean {
        val cal = Calendar.getInstance()
        cal.set(date.year, date.month-1, date.date)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }
    fun isHoliday(date: MonthCalendarDateData): Boolean {
        return false
    }

    fun isThisMonth(date: MonthCalendarDateData): Boolean {
        return date.month == pageData.month
    }

}