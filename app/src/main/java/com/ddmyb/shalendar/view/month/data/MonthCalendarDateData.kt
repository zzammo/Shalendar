package com.ddmyb.shalendar.view.month.data

import com.ddmyb.shalendar.data.Schedule
import com.ddmyb.shalendar.util.MutableLiveListData

data class MonthCalendarDateData(
    val year: Int,
    val month: Int,
    val date: Int,
    val scheduleList: MutableLiveListData<MonthScheduleData>
)
