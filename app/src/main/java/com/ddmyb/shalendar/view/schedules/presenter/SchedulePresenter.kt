package com.ddmyb.shalendar.view.schedules.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ddmyb.shalendar.background_service.alarm.AlarmService
import com.ddmyb.shalendar.domain.Alarm
import com.ddmyb.shalendar.domain.Schedule
import com.ddmyb.shalendar.domain.ScheduleDto
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import com.ddmyb.shalendar.view.schedules.model.service.GeoCodingService
import com.ddmyb.shalendar.view.schedules.model.service.NetworkStatusService
import com.ddmyb.shalendar.view.schedules.model.service.distance_matrix.GoogleDistanceMatrixService
import com.ddmyb.shalendar.view.schedules.model.service.distance_matrix.TMapDistanceMatrixService
import com.ddmyb.shalendar.view.schedules.model.service.fused_location.FusedLocationService
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo
import com.ddmyb.shalendar.view.schedules.utils.DateInfo
import com.ddmyb.shalendar.view.schedules.utils.IterationType
import com.ddmyb.shalendar.view.schedules.utils.MarkerInfo
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.ddmyb.shalendar.util.NewScheduleDto
import com.ddmyb.shalendar.view.schedules.utils.TimeInfo
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId


@RequiresApi(Build.VERSION_CODES.O)
class SchedulePresenter {

    private val geoCodingService: GeoCodingService = GeoCodingService()
    private val fusedLocationService: FusedLocationService

    private val view: ScheduleActivity

    private val schedule: Schedule

    private val alarmInfo: AlarmInfo = AlarmInfo()
    private var iterationType: IterationType = IterationType.NO_REPEAT

    constructor(view: ScheduleActivity,
                newScheduleDto: NewScheduleDto,
                activity: Activity){
        this.view = view
        this.fusedLocationService = FusedLocationService(activity)
        this.schedule = if (newScheduleDto.scheduleId.isNullOrEmpty()){
            val s = Schedule()
            s.startLocalDatetime = Instant.ofEpochMilli(newScheduleDto.mills).atZone(ZoneId.systemDefault()).toLocalDateTime()
            s.endLocalDatetime = s.startLocalDatetime.plusHours(1)
            view.showStartTimeText(TimeInfo( s.startLocalDatetime.hour, s.startLocalDatetime.minute))
            view.showEndTimeText(TimeInfo(s.endLocalDatetime.hour, s.endLocalDatetime.minute))
            view.showStartDateText(s.startLocalDatetime.year, DateInfo(s.startLocalDatetime.monthValue, s.startLocalDatetime.dayOfMonth, s.startLocalDatetime.dayOfWeek.value), true)
            view.showEndDateText(s.endLocalDatetime.year, DateInfo(s.endLocalDatetime.monthValue, s.endLocalDatetime.dayOfMonth, s.endLocalDatetime.dayOfWeek.value), true)
            s
        } else{
            // repository find schedule
            Schedule()
        }
    }

    fun getSchedule(): Schedule {
        return schedule
    }

    fun getIterationType(): IterationType{
        return iterationType
    }
    fun setIterationType(type: IterationType){
        iterationType = type
    }

    fun getAlarmInfo(): AlarmInfo{
        return alarmInfo
    }
    fun setAlarmInfo(type: AlarmInfo.AlarmType){
        alarmInfo.alarmType = type
    }
    fun setAlarmInfo(value: Int, index: Int){
        alarmInfo.updateCustomTime(value, index)
    }

    fun setStartTime(startHour: Int, startMinute: Int) {
        schedule.startLocalDatetime = schedule.startLocalDatetime.withHour(startHour)?.withMinute(startMinute)!!
        view.showStartTimeText(TimeInfo(startHour, startMinute))

        if (schedule.startLocalDatetime != null && schedule.endLocalDatetime != null) {
            val sameDate = schedule.startLocalDatetime.toLocalDate() == schedule.endLocalDatetime.toLocalDate()
            val startTimeAfterEndTime = schedule.startLocalDatetime.isAfter(schedule.endLocalDatetime)

            if (sameDate && startTimeAfterEndTime) {
                schedule.endLocalDatetime = schedule.endLocalDatetime.withHour(startHour).withMinute(startMinute)
                view.showEndTimeText(TimeInfo(schedule.endLocalDatetime.hour, schedule.endLocalDatetime.minute))
            }
        }
    }
    fun setEndTime(endHour: Int, endMinute: Int) {
        schedule.endLocalDatetime = schedule.endLocalDatetime.withHour(endHour)?.withMinute(endMinute)!!
        view.showEndTimeText(TimeInfo(endHour, endMinute))

        if (schedule.startLocalDatetime != null && schedule.endLocalDatetime != null) {
            val sameDate = schedule.startLocalDatetime.toLocalDate() == schedule.endLocalDatetime.toLocalDate()
            val endTimeBeforeStartTime = schedule.endLocalDatetime.isBefore(schedule.startLocalDatetime)

            if (sameDate && endTimeBeforeStartTime) {
                schedule.startLocalDatetime = schedule.startLocalDatetime.withHour(endHour).withMinute(endMinute)
                view.showStartTimeText(TimeInfo(schedule.startLocalDatetime.hour, schedule.startLocalDatetime.minute))
            }
        }
    }


    fun setStartDate(startYear: Int, startMonth: Int, startDay: Int) {
        val newStartDate = schedule.startLocalDatetime.withYear(startYear)?.withMonth(startMonth)?.withDayOfMonth(startDay)

        if (newStartDate != null) {
            schedule.startLocalDatetime = newStartDate
            val startWeek = schedule.startLocalDatetime.dayOfWeek.value
            view.showStartDateText(startYear, DateInfo(startMonth, startDay, startWeek))
            updateDates(schedule.startLocalDatetime.toLocalDate())
        }
    }

    fun setEndDate(endYear: Int, endMonth: Int, endDay: Int) {
        val newEndDate = schedule.endLocalDatetime.withYear(endYear)?.withMonth(endMonth)?.withDayOfMonth(endDay)

        if (newEndDate != null) {
            schedule.endLocalDatetime = newEndDate
            val endWeek = newEndDate.dayOfWeek.value
            view.showEndDateText(endYear, DateInfo(endMonth, endDay, endWeek))
            updateDates(newEndDate.toLocalDate())
        }
    }

    fun saveSchedule(context: Context) {

        schedule.title = view.readTitle()
        schedule.memo = view.readMemo()

        val scheduleDto = ScheduleDto(schedule)
        val alarmService =  AlarmService(context)

        if (alarmInfo.alarmType == AlarmInfo.AlarmType.NULL){
            return
        }
        if (view.isCheckedDepartureAlarmSwitch()){
            val seconds = schedule.dptLocalDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() - alarmInfo.toSeconds()
            val newAlarm = Alarm(scheduleDto)
            alarmService.setAlarmWithTime(seconds, newAlarm)
        }else{
            val seconds = schedule.startLocalDatetime.atZone(ZoneId.systemDefault()).toEpochSecond() - alarmInfo.toSeconds()
            val newAlarm = Alarm(scheduleDto)
            alarmService.setAlarmWithTime(seconds, newAlarm)
        }

        if (NetworkStatusService.isOnline(context)){
            // firebase repository 구현
        }
        // 내부 저장소
    }

    private fun updateDates(newDate: LocalDate) {
        if (newDate.isBefore(schedule.startLocalDatetime.toLocalDate())) {
            schedule.startLocalDatetime = schedule.startLocalDatetime.with(newDate)!!
            view.showStartDateText(schedule.startLocalDatetime.year, DateInfo(schedule.startLocalDatetime.monthValue, schedule.startLocalDatetime.dayOfMonth, schedule.startLocalDatetime.dayOfWeek.value))
        }

        if (newDate.isAfter(schedule.endLocalDatetime.toLocalDate())) {
            schedule.endLocalDatetime = schedule.endLocalDatetime.with(newDate)!!
            view.showEndDateText(
                schedule.endLocalDatetime.year,
                DateInfo(
                    schedule.endLocalDatetime.monthValue,
                    schedule.endLocalDatetime.dayOfMonth,

                    schedule.endLocalDatetime.dayOfWeek.value
                )
            )
        }
    }


    private fun getLocationCallback(context: Context): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    val location = locationList.last()
                    schedule.srcPosition = schedule.srcPosition
                    schedule.srcAddress =
                        geoCodingService.getAddress(schedule.srcPosition, context).toString()

                    val markerSnippet = "위도:${location.latitude} 경도:${location.longitude}"
                    Log.d("googleMap example", "onLocationResult : $markerSnippet")

                    if (schedule.srcPosition == null) {
                        schedule.srcPosition = LatLng(location.latitude, location.longitude)
                    }
                    updateMarker(location, schedule.srcAddress, true)
                }
            }
        }
    }

    fun calTimeRequired(){
        GoogleDistanceMatrixService.execute {
            val costRequired = if (schedule.meansType == MeansType.PUBLIC) {
                GoogleDistanceMatrixService.getTimeRequired(
                    schedule.srcPosition,
                    schedule.dstPosition,
                    schedule.startLocalDatetime,
                    schedule.meansType
                )
            }else{
                TMapDistanceMatrixService.getTimeRequired(
                    schedule.srcPosition,
                    schedule.dstPosition,
                    schedule.meansType)
            }
            schedule.cost = costRequired
            schedule.dptLocalDateTime = schedule.startLocalDatetime.minusSeconds(costRequired.value.toLong())
            view.showTimeRequired(costRequired.text, TimeInfo(schedule.dptLocalDateTime.hour, schedule.dptLocalDateTime.minute))
        }
    }

    fun loadAddressInfo(isSource: Boolean, applicationContext: Context){
        val isOnline: Boolean = NetworkStatusService.isOnline(applicationContext)
        if (isOnline) {
            view.launchAutocompleteGeocodingActivity(isSource)
        } else {
            Toast.makeText(applicationContext, "인터넷 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()

        }
    }

    fun updateMarker(location: Location, markerTitle: String, isSource: Boolean){
        val title = if (isSource) "출발지 : $markerTitle" else "도착지 : $markerTitle"
        val position = LatLng(location.latitude, location.longitude)
        val markerInfo = MarkerInfo(position, title, isSource)
        view.showUpdatedMarker(markerInfo)
    }

    private fun hasLocationPermissions(context: Context): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return hasFineLocationPermission && hasCoarseLocationPermission
    }

    fun requestLocationUpdate(context: Context, checkLocationServicesStatus: Boolean){
        if (hasLocationPermissions(context) && checkLocationServicesStatus ){
            fusedLocationService.updateLiveLocation(getLocationCallback(context))
        }else{
            view.showLocationPermissionsSnackBar()
        }
    }

}