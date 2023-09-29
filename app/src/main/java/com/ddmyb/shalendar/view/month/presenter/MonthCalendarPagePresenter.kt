package com.ddmyb.shalendar.view.month.presenter

import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.data.MonthCalendarDate
import java.util.Calendar

class MonthCalendarPagePresenter(
    val year: Int,
    val month: Int,
    val monthCalendarDateList: MutableList<MonthCalendarDate>
) {

    private var selected: Int

    init {
        val cal = Calendar.getInstance()
        cal.set(year, month-1, 1)

        val dayOfWeek: Int =
            cal.get(Calendar.DAY_OF_WEEK) - 1 //해당 월에 시작하는 요일 -1 을 하면 빈칸을 구할 수 있겠죠 ?
        val max: Int = cal.getActualMaximum(Calendar.DAY_OF_MONTH) // 해당 월에 마지막 요일
        cal.add(Calendar.DATE, -dayOfWeek)

        for (j in 0 until dayOfWeek) {
            monthCalendarDateList.add(
                MonthCalendarDate(
                    year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }
        for (j in 1..max) {
            monthCalendarDateList.add(
                MonthCalendarDate(
                    year,
                    cal.get(Calendar.MONTH)+1,
                    cal.get(Calendar.DATE),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }
        while (monthCalendarDateList.size < 6*7) {
            monthCalendarDateList.add(
                MonthCalendarDate(
                    year,
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
    }

    fun selectDate(idx: Int): Int {
        val preIdx = selected
        selected = idx
        return preIdx
    }

    fun isSaturday(date: MonthCalendarDate): Boolean {
        val cal = Calendar.getInstance()
        cal.set(date.year, date.month-1, date.date)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
    }
    fun isSunday(date: MonthCalendarDate): Boolean {
        val cal = Calendar.getInstance()
        cal.set(date.year, date.month-1, date.date)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }
    fun isHoliday(date: MonthCalendarDate): Boolean {
        return false
    }

    fun isThisMonth(date: MonthCalendarDate): Boolean {
        return date.month == month
    }

}