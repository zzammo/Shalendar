package com.ddmyb.shalendar.view.month.presenter

import android.content.ContentResolver
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.DBRepository
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleRepository
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.util.CalendarProvide
import com.ddmyb.shalendar.util.CalendarProvider
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.util.Logger
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.holiday.HolidayApi
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.lunar.LunarCalendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Calendar
import kotlin.math.log

class MonthLibraryPresenter(
    private val repository: ScheduleRepository
) {

    val scheduleList: MutableMap<LocalDate, MutableLiveListData<ScheduleDto>> = mutableMapOf()
    val externalScheduleList: MutableMap<LocalDate, MutableLiveListData<ScheduleDto>> = mutableMapOf()
    val holidayList: MutableMap<LocalDate, MutableLiveListData<HolidayDTO.HolidayItem>> = mutableMapOf()

    private lateinit var loadScheduleJob: Job
    private lateinit var loadExternalScheduleJob: Job

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

    fun loadSchedule(
        afterEnd: () -> Unit = {},
        ifFail: (Exception) -> Unit = {}) {

        if (::loadScheduleJob.isInitialized && loadScheduleJob.isActive) return

        loadScheduleJob = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
                try {
                    scheduleList.clear()

                    val loadedList = mutableListOf<ScheduleDto>()
                    repository.let {
                        loadedList.addAll(it.readUserAllSchedule())
                    }
                    for (schedule in loadedList) {
                        if (schedule.groupId != "")
                            schedule.color = R.color.google_blue
                    }

                    val cal = Calendar.getInstance()
                    logger.logD("loadSchedule - loadedList size : ${loadedList.size}")
                    for (schedule in loadedList) {
                        val startM = schedule.startMills
                        val endM = schedule.endMills

                        cal.timeInMillis = startM
                        val sYear = cal[Calendar.YEAR]
                        val sMonth = cal[Calendar.MONTH]+1
                        val sDay = cal[Calendar.DAY_OF_MONTH]

                        cal.timeInMillis = endM
                        val eYear = cal[Calendar.YEAR]
                        val eMonth = cal[Calendar.MONTH]+1
                        val eDay = cal[Calendar.DAY_OF_MONTH]

                        addToScheduleList(sYear, sMonth, sDay, schedule)
                        if (!(sYear == eYear && sMonth == eMonth && sDay == eDay))
                            addToScheduleList(eYear, eMonth, eDay, schedule)

                        logger.logD("$schedule ($sYear,$sMonth,$sDay) - ($eYear,$eMonth,$eDay)")
                    }

                    withContext(Dispatchers.Main) {
                        afterEnd()
                    }
                } catch (e: Exception) {
                    logger.logE("message: ${e.message}")
                    for (stack in e.stackTrace) {
                        logger.logE("className: ${stack.className}\n" +
                                "fileName: ${stack.fileName}\n" +
                                "lineNumber: ${stack.lineNumber}\n" +
                                "isNativeMethod: ${stack.isNativeMethod}\n" +
                                "methodName: ${stack.methodName}")
                    }
                    withContext(Dispatchers.Main) {
                        ifFail(e)
                    }
                }
        }
        loadScheduleJob.start()
    }

    fun loadGroupSchedule(
        groupId: String,
        afterEnd: () -> Unit = {},
        ifFail: (Exception) -> Unit = {}) {

        if (::loadScheduleJob.isInitialized && loadScheduleJob.isActive) return

        loadScheduleJob = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
            try {
                scheduleList.clear()

                val loadedList = mutableListOf<ScheduleDto>()
                repository.let {
                    loadedList.addAll(it.readGroupSchedule(groupId))
                }
                for (schedule in loadedList) {
                    if (schedule.userId != UserRepository.getInstance()!!.getUserId())
                        schedule.title = "다른 사람 일정"
                    else if(schedule.groupId != groupId)
                        schedule.title = "${schedule.groupId}-${schedule.title}"
                }

                val cal = Calendar.getInstance()
                for (schedule in loadedList) {
                    val startM = schedule.startMills
                    val endM = schedule.endMills

                    cal.timeInMillis = startM
                    val sYear = cal[Calendar.YEAR]
                    val sMonth = cal[Calendar.MONTH]+1
                    val sDay = cal[Calendar.DAY_OF_MONTH]

                    cal.timeInMillis = endM
                    val eYear = cal[Calendar.YEAR]
                    val eMonth = cal[Calendar.MONTH]+1
                    val eDay = cal[Calendar.DAY_OF_MONTH]

                    addToScheduleList(sYear, sMonth, sDay, schedule)
                    if (!(sYear == eYear && sMonth == eMonth && sDay == eDay))
                        addToScheduleList(eYear, eMonth, eDay, schedule)

                    logger.logD("$schedule ($sYear,$sMonth,$sDay) - ($eYear,$eMonth,$eDay)")
                }

                withContext(Dispatchers.Main) {
                    afterEnd()
                }
            } catch (e: Exception) {
                logger.logE("message: ${e.message}")
                for (stack in e.stackTrace) {
                    logger.logE("className: ${stack.className}\n" +
                            "fileName: ${stack.fileName}\n" +
                            "lineNumber: ${stack.lineNumber}\n" +
                            "isNativeMethod: ${stack.isNativeMethod}\n" +
                            "methodName: ${stack.methodName}")
                }
                withContext(Dispatchers.Main) {
                    ifFail(e)
                }
            }
        }
        loadScheduleJob.start()
    }

    fun loadExternalSchedule(
        contentResolver: ContentResolver,
        calendarId: Int,
        afterEnd: () -> Unit,
        ifFail: (Exception) -> Unit = {}
    ) {

        if (::loadExternalScheduleJob.isInitialized && loadExternalScheduleJob.isActive) return

        loadExternalScheduleJob = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
            try {
                val cal = Calendar.getInstance()

                logger.logD("loadExternalSchedule - calendarId: $calendarId")

                externalScheduleList.clear()

                CalendarProvider.
                getEvents(contentResolver, calendarId,
                    {
                        val schedule = it.toScheduleDto()

                        schedule.userId = UserRepository.getInstance()!!.getUserId()

                        val startM = it.start
                        val endM = it.end

                        cal.timeInMillis = startM
                        val sYear = cal[Calendar.YEAR]
                        val sMonth = cal[Calendar.MONTH]+1
                        val sDay = cal[Calendar.DAY_OF_MONTH]

                        cal.timeInMillis = endM
                        val eYear = cal[Calendar.YEAR]
                        val eMonth = cal[Calendar.MONTH]+1
                        val eDay = cal[Calendar.DAY_OF_MONTH]

                        addToExternalScheduleList(sYear, sMonth, sDay, schedule)
                        if (!(sYear == eYear && sMonth == eMonth && sDay == eDay) && it.allDay == 0)
                            addToExternalScheduleList(eYear, eMonth, eDay, schedule)
                    },
                    {
                        afterEnd()
                    }
                )
            } catch (e: Exception) {
                logger.logE("message: ${e.message}")
                for (stack in e.stackTrace) {
                    logger.logE("className: ${stack.className}\n" +
                            "fileName: ${stack.fileName}\n" +
                            "lineNumber: ${stack.lineNumber}\n" +
                            "isNativeMethod: ${stack.isNativeMethod}\n" +
                            "methodName: ${stack.methodName}")
                }
                withContext(Dispatchers.Main) {
                    ifFail(e)
                }
            }

        }
        loadExternalScheduleJob.start()
    }

    fun holidayListToScheduleList(holidayList: List<HolidayDTO.HolidayItem>): List<ScheduleDto> {
        val sList = mutableListOf<ScheduleDto>()
        for (holiday in holidayList) {
            sList.add(
                ScheduleDto(
                    userId = UserRepository.getInstance()!!.getUserId(),
                    title = holiday.dateName,
                    color = R.color.cat_1
                )
            )
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

    private fun dateToYYYYMMDD(year: Int, month: Int, date: Int): String {
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

    fun toLunar(date: LocalDate): String {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth

        val yyyymmdd = dateToYYYYMMDD(year, month, day)
        val lunar = LunarCalendar.SolarToLunar(yyyymmdd)

//        val lunarYear = lunar.substring(0, 4).toInt()
//        val lunarMonth = lunar.substring(4, 6).toInt()
//        val lunarDay = lunar.substring(6, 8).toInt()

        return lunar
    }


    private fun addToScheduleList(year: Int, month: Int, day: Int, loadedList: ScheduleDto) {
        if (scheduleList[LocalDate.of(year, month, day)] == null)
            scheduleList[LocalDate.of(year, month, day)] = MutableLiveListData()

        scheduleList[LocalDate.of(year, month, day)]!!.add(loadedList)
    }

    private fun addToExternalScheduleList(year: Int, month: Int, day: Int, loadedList: ScheduleDto) {
        if (externalScheduleList[LocalDate.of(year, month, day)] == null)
            externalScheduleList[LocalDate.of(year, month, day)] = MutableLiveListData()

        externalScheduleList[LocalDate.of(year, month, day)]!!.add(loadedList)
    }
}