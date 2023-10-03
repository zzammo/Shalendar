package com.ddmyb.shalendar.view.schedules

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.domain.Schedule
import com.ddmyb.shalendar.view.schedules.adapter.GeoCodingService
import com.ddmyb.shalendar.view.schedules.adapter.ScheduleService
import com.ddmyb.shalendar.view.schedules.distance.adapter.RetrofitImpl
import com.ddmyb.shalendar.view.schedules.distance.model.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.StartDateTimeDto
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
class SchedulePresenter(
    val view: SchedulesContract.View,
    private val startDateTimeDto: StartDateTimeDto) : SchedulesContract.Presenter {

    private val scheduleService: ScheduleService = ScheduleService(this)
    private val geoCodingService: GeoCodingService = GeoCodingService()

    private val schedule: Schedule = if (startDateTimeDto.scheduleId.isNullOrEmpty()){
        val s = Schedule()
        s.startLocalDateTime = startDateTimeDto.dateTime
        s.endLocalDateTime = s.startLocalDateTime!!.plusHours(1)
        s
    }
    else{
    // repository find schedule
    Schedule()
    }

    override fun getSchedule(): Schedule {
        return schedule
    }

    override fun setStartTime(startHour: Int, startMinute: Int) {
        schedule.startLocalDateTime = schedule.startLocalDateTime?.withHour(startHour)?.withMinute(startMinute)
        view.showStartTimeText(startHour, startMinute)

        if (schedule.startLocalDateTime != null && schedule.endLocalDateTime != null) {
            val sameDate = schedule.startLocalDateTime!!.toLocalDate() == schedule.endLocalDateTime!!.toLocalDate()
            val startTimeAfterEndTime = schedule.startLocalDateTime!!.isAfter(schedule.endLocalDateTime)

            if (sameDate && startTimeAfterEndTime) {
                schedule.endLocalDateTime = schedule.endLocalDateTime!!.withHour(startHour).withMinute(startMinute)
                view.showEndTimeText(schedule.endLocalDateTime!!.hour, schedule.endLocalDateTime!!.minute)
            }
        }
    }
    override fun setEndTime(endHour: Int, endMinute: Int) {
        schedule.endLocalDateTime = schedule.endLocalDateTime?.withHour(endHour)?.withMinute(endMinute)
        view.showEndTimeText(endHour, endMinute)

        if (schedule.startLocalDateTime != null && schedule.endLocalDateTime != null) {
            val sameDate = schedule.startLocalDateTime!!.toLocalDate() == schedule.endLocalDateTime!!.toLocalDate()
            val endTimeBeforeStartTime = schedule.endLocalDateTime!!.isBefore(schedule.startLocalDateTime)

            if (sameDate && endTimeBeforeStartTime) {
                schedule.startLocalDateTime = schedule.startLocalDateTime!!.withHour(endHour).withMinute(endMinute)
                view.showStartTimeText(schedule.startLocalDateTime!!.hour, schedule.startLocalDateTime!!.minute)
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun setStartDate(startYear: Int, startMonth: Int, startDay: Int) {
        val newStartDate = schedule.startLocalDateTime?.withYear(startYear)?.withMonth(startMonth)?.withDayOfMonth(startDay)

        if (newStartDate != null) {
            schedule.startLocalDateTime = newStartDate
            val startWeek = schedule.startLocalDateTime!!.dayOfWeek.value
            view.showStartDateText(startMonth, startDay, startWeek)
            updateDates(schedule.startLocalDateTime!!.toLocalDate())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun setEndDate(endYear: Int, endMonth: Int, endDay: Int) {
        val newEndDate = schedule.endLocalDateTime?.withYear(endYear)?.withMonth(endMonth)?.withDayOfMonth(endDay)

        if (newEndDate != null) {
            schedule.endLocalDateTime = newEndDate
            val endWeek = newEndDate.dayOfWeek.value
            view.showEndDateText(endMonth, endDay, endWeek)
            updateDates(newEndDate.toLocalDate())
        }
    }


    override fun saveSchedule() {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDates(newDate: LocalDate) {
        if (newDate.isBefore(schedule.startLocalDateTime?.toLocalDate())) {
            schedule.startLocalDateTime = schedule.startLocalDateTime?.with(newDate)
            view.showStartDateText(schedule.startLocalDateTime!!.monthValue, schedule.startLocalDateTime!!.dayOfMonth, schedule.startLocalDateTime!!.dayOfWeek.value)
        }

        if (newDate.isAfter(schedule.endLocalDateTime?.toLocalDate())) {
            schedule.endLocalDateTime = schedule.endLocalDateTime?.with(newDate)
            view.showEndDateText(schedule.endLocalDateTime!!.monthValue, schedule.endLocalDateTime!!.dayOfMonth, schedule.endLocalDateTime!!.dayOfWeek.value)
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
                    schedule.srcAddress =
                        geoCodingService.getAddress(schedule.srcPosition!!, context).toString()

                    val markerSnippet = "위도:${location.latitude.toString()} 경도:${location.longitude.toString()}"
                    Log.d("googleMap example", "onLocationResult : $markerSnippet")

                    if (schedule.srcLocation == null) {
                        schedule.srcLocation = location
                    }

                    view.setSrcLocation(
                        schedule.srcLocation!!,
                        schedule.srcAddress!!
                    )
                }
            }
        }
    }

    override fun getMarkerTitle(context: Context): String? {
        val currentAddress = geoCodingService.getAddress(schedule.srcPosition!!, context)
        Log.d("currentAddress", currentAddress.toString())
        return currentAddress
    }

    override fun calExpectedTime(){
        var costRequired: TextValueObject? = null
        RetrofitImpl.execute {
            costRequired = getTimeRequired(
                schedule.srcPosition!!,
                schedule.dstPosition!!,
                schedule.startLocalDateTime!!,
                schedule.meansType
            )
            schedule.cost = costRequired!!
            schedule.departureLocalDateTime = schedule.startLocalDateTime!!.minusMinutes(costRequired!!.value.toLong())
            view.showTimeRequired(costRequired!!.text, schedule.departureLocalDateTime!!)
        }
    }

}