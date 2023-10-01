package com.ddmyb.shalendar.view.month.presenter

import com.ddmyb.shalendar.util.CalendarFunc
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.data.MonthCalendarDateData
import com.ddmyb.shalendar.view.month.data.MonthPageData
import com.ddmyb.shalendar.view.month.data.MonthScheduleData
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
            MonthScheduleData(
                "name",
                0L,
                1000L
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            MonthScheduleData(
                "name",
                0L,
                1000L
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            MonthScheduleData(
                "name",
                0L,
                1000L
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            MonthScheduleData(
                "name",
                0L,
                1000L
            )
        )
        pageData.calendarDateList[idx].scheduleList.add(
            MonthScheduleData(
                "name",
                0L,
                1000L
            )
        )
    }

    fun selectDate(idx: Int): Int {
        val preIdx = selected
        selected = idx
        return preIdx
    }

    fun getWeekOfDay(date: MonthCalendarDateData): String {
        return CalendarFunc.dayOfWeekOfDate(date.year, date.month, date.date)
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