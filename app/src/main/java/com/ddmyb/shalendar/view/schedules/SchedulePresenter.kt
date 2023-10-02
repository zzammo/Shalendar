package com.ddmyb.shalendar.view.schedules

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.adapter.GeoCodingService
import com.ddmyb.shalendar.view.schedules.adapter.ScheduleService
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate

class SchedulePresenter(
    val view: SchedulesContract.View,
    private val scheduleId: String) : SchedulesContract.Presenter {

    val scheduleService: ScheduleService = ScheduleService(this)
    val geoCodingService: GeoCodingService = GeoCodingService()

    private val schedule: Schedule
    init {
        schedule =
            if (scheduleId.isEmpty()){
            // repository create schedule
            Schedule() }
            else{
            // repository find schedule
            Schedule()
            }
    }

    override fun getSchedule(): Schedule{
        return schedule
    }

    override fun setStartTime(startHour: Int, startMinute: Int) {
        schedule.startHour = startHour
        schedule.startMinute
        view.showStartTimeText(startHour, startMinute)
        val sameDate = schedule.startYear == schedule.endYear && schedule.startMonth == schedule.endMonth && schedule.startDay == schedule.endDay
        if (sameDate && (startHour > schedule.endHour || (startHour == schedule.endHour && startMinute > schedule.endMinute))) {
            schedule.endHour = startHour
            schedule.endMinute = startMinute
            view.showEndTimeText(schedule.endHour, schedule.endMinute)
        }
    }


    override fun setEndTime(endHour: Int, endMinute: Int) {
        schedule.endHour = endHour
        schedule.endMinute = endMinute
        view.showEndTimeText(endHour, endMinute)
        val sameDate = schedule.startYear == schedule.endYear && schedule.startMonth == schedule.endMonth && schedule.startDay == schedule.endDay
        if (sameDate && (schedule.startHour > endHour || (schedule.startHour == endHour && schedule.startMinute > endMinute))) {
            schedule.startHour = endHour
            schedule.startMinute = endMinute
            view.showStartTimeText(schedule.startHour, schedule.startMinute)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setStartDate(startYear: Int, startMonth: Int, startDay: Int) {
        Log.d("startMonth", startMonth.toString())
        val startDate = LocalDate.of(startYear, startMonth, startDay)
        val startWeek = startDate.dayOfWeek.value
        view.showStartDateText(startMonth, startDay, startWeek)

        schedule.startYear = startYear
        schedule.startMonth = startMonth
        schedule.startDay = startDay
        schedule.startWeek = startDate.dayOfWeek.value

        updateDates(startDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setEndDate(endYear: Int, endMonth: Int, endDay: Int) {
        val endDate = LocalDate.of(endYear, endMonth, endDay)
        val endWeek = endDate.dayOfWeek.value
        view.showEndDateText(endMonth, endDay, endWeek)

        schedule.endYear = endYear
        schedule.endMonth = endMonth
        schedule.endDay = endDay
        schedule.endWeek = endDate.dayOfWeek.value

        updateDates(endDate)
    }

    override fun saveSchedule() {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDates(newDate: LocalDate) {
        val isStartDateUpdated = newDate.isBefore(LocalDate.of(schedule.startYear, schedule.startMonth, schedule.startDay))
        val isEndDateUpdated = newDate.isAfter(LocalDate.of(schedule.endYear, schedule.endMonth, schedule.endDay))

        if (isStartDateUpdated || isEndDateUpdated) {
            schedule.startYear = newDate.year
            schedule.startMonth = newDate.monthValue
            schedule.startDay = newDate.dayOfMonth
            schedule.startWeek = newDate.dayOfWeek.value

            view.showStartDateText(schedule.startMonth, schedule.startDay, schedule.startWeek)
        }
        if (isEndDateUpdated){
            schedule.endYear = newDate.year
            schedule.endMonth = newDate.monthValue
            schedule.endDay = newDate.dayOfMonth
            schedule.endWeek = newDate.dayOfWeek.value

            view.showEndDateText(schedule.endMonth, schedule.endDay, schedule.endWeek)
        }
    }

    override fun getLocationCallback(context: Context): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    val location = locationList.last()
                    schedule.srcPosition = schedule.srcPosition ?: LatLng(location.latitude, location.longitude)
                    schedule.srcAddress = schedule.srcAddress ?: geoCodingService.getCurrentAddress(schedule.srcPosition!!, context)

                    val markerSnippet = "위도:${location.latitude.toString()} 경도:${location.longitude.toString()}"
                    Log.d("googleMap example", "onLocationResult : $markerSnippet")

                    if (schedule.srcLocation == null) {
                        schedule.srcLocation = location
                    }

                    schedule.dstLocation?.let { dstLocation ->
//                        view.setDstLocation(
//                            dstLocation,
//                            schedule.dstAddress!!,
//                            "위도:${dstLocation.latitude.toString()} 경도:${dstLocation.longitude.toString()}"
//                        )
                    }

//                    view.setSrcLocation(
//                        schedule.srcLocation!!,
//                        schedule.srcAddress!!,
//                        "위도:${schedule.srcLocation!!.latitude.toString()} 경도:${schedule.srcLocation!!.longitude.toString()}"
//                    )
                }
            }
        }
    }

    override fun getMarkerTitle(context: Context): String? {
        return geoCodingService.getCurrentAddress(schedule.srcPosition!!, context)
    }

}