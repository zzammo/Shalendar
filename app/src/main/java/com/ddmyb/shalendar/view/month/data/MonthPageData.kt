package com.ddmyb.shalendar.view.month.data

import androidx.lifecycle.MutableLiveData
import com.ddmyb.shalendar.util.MutableLiveListData

data class MonthPageData(
    val year: Int,
    val month: Int,
    val calendarDateList: MutableLiveListData<MonthCalendarDateData>
)
