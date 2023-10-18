package com.ddmyb.shalendar.view.month.data

import android.graphics.Color
import com.ddmyb.shalendar.util.MutableLiveListData
import java.util.Calendar

data class MonthCalendarDateData(
    val year: Int,
    val month: Int,
    val date: Int,
    var isHoliday: Boolean,
    val lunar: String,
    val scheduleList: MutableLiveListData<ScheduleData>
) {
    fun getColor(): Int {
        return if (isSaturday())
            Color.BLUE
        else if(isSunday() || isHoliday)
            Color.RED
        else
            Color.BLACK
    }

    fun isSaturday(): Boolean {
        val cal = Calendar.getInstance()
        cal.set(year, month-1, date)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY
    }
    fun isSunday(): Boolean {
        val cal = Calendar.getInstance()
        cal.set(year, month-1, date)
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

}
