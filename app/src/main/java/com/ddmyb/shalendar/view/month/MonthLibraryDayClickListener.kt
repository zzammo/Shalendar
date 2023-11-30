package com.ddmyb.shalendar.view.month

import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.data.ScheduleData

interface MonthLibraryDayClickListener {
    fun click(year: Int, month: Int, day: Int, groupId: String?, scheduleList: MutableList<ScheduleDto>)
    fun doubleClick(year: Int, month: Int, day: Int, groupId: String?, scheduleList: MutableList<ScheduleDto>)
}