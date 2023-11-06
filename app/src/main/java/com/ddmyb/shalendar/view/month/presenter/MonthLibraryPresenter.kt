package com.ddmyb.shalendar.view.month.presenter

import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.lunar.LunarCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class MonthLibraryPresenter {

    val scheduleList: MutableMap<LocalDate, MutableLiveListData<ScheduleDto>> = mutableMapOf()
    val holidayList: MutableMap<LocalDate, MutableLiveListData<HolidayDTO.HolidayItem>> = mutableMapOf()

    private val logger = Logger("MonthLibraryPresenter", true)

    fun loadHoliday(year: Int, month: Int, afterSuccess: () -> Unit = {}) {
        HolidayApi.getHolidays(
            year,
            month,
            object: HttpResult<List<HolidayDTO.HolidayItem>> {
                override fun success(data: List<HolidayDTO.HolidayItem>) {
                    val date = LocalDate.of(year, month, 1)
                    for (day in 1..date.lengthOfMonth()) {
                        if (holidayList[LocalDate.of(year, month, day)] == null)
                            holidayList[LocalDate.of(year, month, day)] = MutableLiveListData()
                        for (item in data) {
                            if (item.locdate%100 == day)
                                holidayList[LocalDate.of(year, month, day)]!!.add(item)
                        }
                        logger.logD("loadHoliday ${holidayList[LocalDate.of(year, month, day)]?.list}")
                    }
                    afterSuccess()
                }

                override fun appFail() {
                    logger.logD("loadHoliday appFail")
                }

                override fun fail(throwable: Throwable) {
                    logger.logD("loadHoliday fail\n${throwable.message}")
                }

                override fun finally() {

                }
            }
        )
    }

    fun loadSchedule(year: Int, month: Int, day: Int, afterEnd: () -> Unit = {}) {
        //TODO: load schedules

        if (scheduleList[LocalDate.of(year, month, day)] == null)
            scheduleList[LocalDate.of(year, month, day)] = MutableLiveListData()

        scheduleList[LocalDate.of(year, month, day)]!!.clear()

        scheduleList[LocalDate.of(year, month, day)]!!.addAll(
            mutableListOf(ScheduleDto(title="test1", color = R.color.cat_3),
                ScheduleDto(title="test2", color = R.color.cat_5)))

        afterEnd()
    }

    fun loadGroupSchedule(groupId: String) {

    }

    fun loadData(startMonth: YearMonth,
                 endMonth: YearMonth,
                 currentMonth: YearMonth,
                 afterEnd: (YearMonth) -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            var nowMonth = YearMonth.of(currentMonth.year, currentMonth.monthValue)
            var year: Int
            var month: Int
            var cnt = 0

            while (true) {
                logger.logD("loadData: toEnd ${nowMonth.year}, ${nowMonth.monthValue}")
                year = nowMonth.year
                month = nowMonth.monthValue

                for (day in 1..nowMonth.atEndOfMonth().dayOfMonth) {
                    loadSchedule(year, month, day)
                }
                loadHoliday(year, month) {
                    afterEnd(nowMonth)
                }
                if ((nowMonth.year == endMonth.year) && (nowMonth.monthValue == endMonth.monthValue))
                    break
                delay(cnt*500L)

                nowMonth = nowMonth.plusMonths(1)
                cnt++
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            var nowMonth = YearMonth.of(currentMonth.year, currentMonth.monthValue)
            var year: Int
            var month: Int
            var cnt = 0

            while (true) {
                logger.logD("loadData: toStart ${nowMonth.year}, ${nowMonth.monthValue}")
                year = nowMonth.year
                month = nowMonth.monthValue

                for (day in 1..nowMonth.atEndOfMonth().dayOfMonth) {
                    loadSchedule(year, month, day)
                }
                loadHoliday(year, month) {
                    afterEnd(nowMonth)
                }
                if ((nowMonth.year == startMonth.year) && (nowMonth.monthValue == startMonth.monthValue))
                    break
                delay(cnt*500L)

                nowMonth = nowMonth.minusMonths(1)
                cnt++
            }
        }
    }

    fun holidayListToScheduleList(holidayList: List<HolidayDTO.HolidayItem>): List<ScheduleDto> {
        val sList = mutableListOf<ScheduleDto>()
        for (holiday in holidayList) {
            sList.add(ScheduleDto(title = holiday.dateName, color = R.color.cat_1))
        }
        return sList
    }

    fun isSaturday(date: LocalDate): Boolean {
        return date.dayOfWeek.value == DayOfWeek.SATURDAY.value
    }
    fun isSunday(date: LocalDate): Boolean {
        return date.dayOfWeek.value == DayOfWeek.SUNDAY.value
    }
    fun isHoliday(date: LocalDate): Boolean {
        val holidayList = holidayList[LocalDate.of(date.year, date.monthValue, date.dayOfMonth)]?: MutableLiveListData()
        return holidayList.list.size != 0
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

    fun toLunar(date: LocalDate): LocalDate {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        val yyyymmdd = dateToYYYYMMDD(year, month, day)
        val lunar = LunarCalendar.LunarToSolar(yyyymmdd)

        val lunarYear = lunar.substring(0, 4).toInt()
        val lunarMonth = lunar.substring(4, 6).toInt()
        val lunarDay = lunar.substring(6, 8).toInt()

        return LocalDate.of(lunarYear, lunarMonth, lunarDay)
    }

}