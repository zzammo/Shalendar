package com.ddmyb.shalendar.view.month.data

import com.ddmyb.shalendar.util.MutableLiveListData

data class TimeTableScheduleList(
    val name: String,
    val list: MutableList<ScheduleData>
)
