package com.ddmyb.shalendar.view.schedules

import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationCallback
import org.json.JSONObject
import java.time.Month

interface SchedulesContract {
    interface View{
        fun showEndTimeText(endHour: Int, endMinute: Int)

        fun showStartTimeText(startHour: Int, startMinute: Int)
        fun showStartDateText(startMonth: Int, startDay: Int, startWeek: Int)
        fun showEndDateText(endMonth: Int, endDay: Int, endWeek: Int)

//        fun setSrcLocation(location: Location, markerTitle: String, markerSnippet: String?)
//        fun setDstLocation(location: Location, markerTitle: String, markerSnippet: String?)

    }
    interface Presenter {
        fun setStartTime(startHour: Int, startMinute: Int)
        fun setEndTime(endHour: Int, endMinute: Int)
        fun setStartDate(startYear: Int, startMonth: Int, startDay: Int)
        fun setEndDate(endYear: Int, endMonth: Int, endDay: Int)
        fun saveSchedule()
        fun getSchedule(): Schedule
        fun getLocationCallback(context: Context): LocationCallback
        fun getMarkerTitle(context: Context): String?
    }
}