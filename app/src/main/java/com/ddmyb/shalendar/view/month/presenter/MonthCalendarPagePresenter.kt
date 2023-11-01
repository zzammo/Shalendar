package com.ddmyb.shalendar.view.month.presenter

import android.graphics.Color
import android.util.Log
import com.ddmyb.shalendar.domain.ScheduleDto
import com.ddmyb.shalendar.util.CalendarFunc
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.lunar.LunarCalendar
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
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)+1
            val date = cal.get(Calendar.DATE)
            pageData.calendarDateList.add(
                MonthCalendarDateData(
                    year,
                    month,
                    date,
                    false,
                    toLunar(dateToYYYYMMDD(year, month, date)),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }
        for (j in 1..max) {
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)+1
            val date = cal.get(Calendar.DATE)
            pageData.calendarDateList.add(
                MonthCalendarDateData(
                    year,
                    month,
                    date,
                    false,
                    toLunar(dateToYYYYMMDD(year, month, date)),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }
        while (pageData.calendarDateList.value!!.size < 6 * 7) {
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)+1
            val date = cal.get(Calendar.DATE)
            pageData.calendarDateList.add(
                MonthCalendarDateData(
                    year,
                    month,
                    date,
                    false,
                    toLunar(dateToYYYYMMDD(year, month, date)),
                    MutableLiveListData()
                )
            )
            cal.add(Calendar.DATE, 1)
        }

        selected = -1
    }

    fun loadHoliday(httpResult: HttpResult<List<HolidayDTO.HolidayItem>>) {
        Log.d("Presenter", "${pageData.year}, ${pageData.month}")
        HolidayApi.getHolidays(
            pageData.year,
            pageData.month,
            httpResult
        )
    }

    fun loadSchedule(idx: Int) {
        //TODO: load schedules
        pageData.calendarDateList.value!![idx].scheduleList.add(
            ScheduleDto(title="n1")
        )
        pageData.calendarDateList.value!![idx].scheduleList.add(
            ScheduleDto(title="n2")
        )
//        pageData.calendarDateList[idx].scheduleList.add(
//            ScheduleData(
//                "n3",
//                (1000 * 60 * 60) * 12L,
//                (1000 * 60 * 60) * 14L,
//                Color.CYAN
//            )
//        )
//        pageData.calendarDateList[idx].scheduleList.add(
//            ScheduleData(
//                "n4",
//                (1000 * 60 * 60) * 15L,
//                (1000 * 60 * 60) * 19L,
//                Color.GRAY
//            )
//        )
//        pageData.calendarDateList[idx].scheduleList.add(
//            ScheduleData(
//                "n5",
//                (1000 * 60 * 60) * 20L,
//                ((1000 * 60 * 60) * 20.5).toLong(),
//                Color.YELLOW
//            )
//        )
    }

    fun findDate(year: Int, month: Int, date: Int): Int? {
        for (i in pageData.calendarDateList.value!!.indices) {
            val dateData = pageData.calendarDateList.value!![i]
            if (dateData.year == year &&
                dateData.month == month &&
                dateData.date == date)
                return i
        }
        return null
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
        return date.isSaturday()
    }
    fun isSunday(date: MonthCalendarDateData): Boolean {
        return date.isSunday()
    }
    fun isHoliday(date: MonthCalendarDateData): Boolean {
        return date.isHoliday
    }

    fun isThisMonth(date: MonthCalendarDateData): Boolean {
        return date.month == pageData.month
    }

    fun dateToYYYYMMDD(year: Int, month: Int, date: Int): String {
        val yyyy = year.toString()
        val mm = when(month) {
            in 1 .. 9 -> "0${month}"
            else -> "$month"
        }
        val dd = when(date) {
            in 1 .. 9 -> "0${date}"
            else -> "$date"
        }
        return "$yyyy$mm$dd"
    }

    fun toLunar(yyyymmdd: String): String {
        val lunar = LunarCalendar.LunarToSolar(yyyymmdd)
        return "${lunar.substring(4, 6)}/${lunar.substring(6, 8)}"
    }

}