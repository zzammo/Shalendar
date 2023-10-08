package com.ddmyb.shalendar.view.schedules.presenter

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.ddmyb.shalendar.background_service.alarm.AlarmService
import com.ddmyb.shalendar.domain.Schedule
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
import com.ddmyb.shalendar.view.schedules.utils.StartDateTimeDto
import com.ddmyb.shalendar.view.schedules.utils.TimeInfo
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset


@RequiresApi(Build.VERSION_CODES.O)
class SchedulePresenter {

    private val geoCodingService: GeoCodingService = GeoCodingService()
    private val fusedLocationService: FusedLocationService

    private val view: ScheduleActivity

    private val schedule: Schedule

    private val alarmInfo: AlarmInfo = AlarmInfo()
    private var iterationType: IterationType = IterationType.NO_REPEAT

    constructor(view: ScheduleActivity,
                startDateTimeDto: StartDateTimeDto,
                activity: Activity){
        this.view = view
        this.fusedLocationService = FusedLocationService(activity)
        this.schedule = if (startDateTimeDto.scheduleId.isNullOrEmpty()){
            val s = Schedule()
            s.startLocalDateTime = startDateTimeDto.dateTime
            s.endLocalDateTime = s.startLocalDateTime!!.plusHours(1)
            view.showStartTimeText(TimeInfo( s.startLocalDateTime!!.hour, s.startLocalDateTime!!.minute))
            view.showEndTimeText(TimeInfo(s.endLocalDateTime!!.hour, s.endLocalDateTime!!.minute))
            view.showStartDateText(s.startLocalDateTime!!.year, DateInfo(s.startLocalDateTime!!.monthValue, s.startLocalDateTime!!.dayOfMonth, s.startLocalDateTime!!.dayOfWeek.value), true)
            view.showEndDateText(s.endLocalDateTime!!.year, DateInfo(s.endLocalDateTime!!.monthValue, s.endLocalDateTime!!.dayOfMonth, s.endLocalDateTime!!.dayOfWeek.value), true)
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
        schedule.startLocalDateTime = schedule.startLocalDateTime?.withHour(startHour)?.withMinute(startMinute)
        view.showStartTimeText(TimeInfo(startHour, startMinute))

        if (schedule.startLocalDateTime != null && schedule.endLocalDateTime != null) {
            val sameDate = schedule.startLocalDateTime!!.toLocalDate() == schedule.endLocalDateTime!!.toLocalDate()
            val startTimeAfterEndTime = schedule.startLocalDateTime!!.isAfter(schedule.endLocalDateTime)

            if (sameDate && startTimeAfterEndTime) {
                schedule.endLocalDateTime = schedule.endLocalDateTime!!.withHour(startHour).withMinute(startMinute)
                view.showEndTimeText(TimeInfo(schedule.endLocalDateTime!!.hour, schedule.endLocalDateTime!!.minute))
            }
        }
    }
    fun setEndTime(endHour: Int, endMinute: Int) {
        schedule.endLocalDateTime = schedule.endLocalDateTime?.withHour(endHour)?.withMinute(endMinute)
        view.showEndTimeText(TimeInfo(endHour, endMinute))

        if (schedule.startLocalDateTime != null && schedule.endLocalDateTime != null) {
            val sameDate = schedule.startLocalDateTime!!.toLocalDate() == schedule.endLocalDateTime!!.toLocalDate()
            val endTimeBeforeStartTime = schedule.endLocalDateTime!!.isBefore(schedule.startLocalDateTime)

            if (sameDate && endTimeBeforeStartTime) {
                schedule.startLocalDateTime = schedule.startLocalDateTime!!.withHour(endHour).withMinute(endMinute)
                view.showStartTimeText(TimeInfo(schedule.startLocalDateTime!!.hour, schedule.startLocalDateTime!!.minute))
            }
        }
    }


    fun setStartDate(startYear: Int, startMonth: Int, startDay: Int) {
        val newStartDate = schedule.startLocalDateTime?.withYear(startYear)?.withMonth(startMonth)?.withDayOfMonth(startDay)

        if (newStartDate != null) {
            schedule.startLocalDateTime = newStartDate
            val startWeek = schedule.startLocalDateTime!!.dayOfWeek.value
            view.showStartDateText(startYear, DateInfo(startMonth, startDay, startWeek))
            updateDates(schedule.startLocalDateTime!!.toLocalDate())
        }
    }

    fun setEndDate(endYear: Int, endMonth: Int, endDay: Int) {
        val newEndDate = schedule.endLocalDateTime?.withYear(endYear)?.withMonth(endMonth)?.withDayOfMonth(endDay)

        if (newEndDate != null) {
            schedule.endLocalDateTime = newEndDate
            val endWeek = newEndDate.dayOfWeek.value
            view.showEndDateText(endYear, DateInfo(endMonth, endDay, endWeek))
            updateDates(newEndDate.toLocalDate())
        }
    }
    private fun handleAlarm(context: Context){
        val alarmService =  AlarmService(context)
        if (alarmInfo.alarmType == AlarmInfo.AlarmType.NULL){
            return
        }
        if (view.isCheckedDepartureAlarmSwitch()){
            val seconds = schedule.departureLocalDateTime!!.atZone(ZoneId.systemDefault()).toEpochSecond() - alarmInfo.toSeconds()
            alarmService.setAlarmWithTime(seconds)
        }else{
            val seconds = schedule.startLocalDateTime!!.atZone(ZoneId.systemDefault()).toEpochSecond() - alarmInfo.toSeconds()
            alarmService.setAlarmWithTime(seconds)
        }
    }

    fun saveSchedule(context: Context) {
        schedule.title = view.readTitle()
        schedule.memo = view.readMemo()
        handleAlarm(context)
        if (NetworkStatusService.isOnline(context)){
            // firebase repository 구현
        }
        // 내부 저장소
    }

    private fun updateDates(newDate: LocalDate) {
        if (newDate.isBefore(schedule.startLocalDateTime?.toLocalDate())) {
            schedule.startLocalDateTime = schedule.startLocalDateTime?.with(newDate)
            view.showStartDateText(schedule.startLocalDateTime!!.year, DateInfo(schedule.startLocalDateTime!!.monthValue, schedule.startLocalDateTime!!.dayOfMonth, schedule.startLocalDateTime!!.dayOfWeek.value))
        }

        if (newDate.isAfter(schedule.endLocalDateTime?.toLocalDate())) {
            schedule.endLocalDateTime = schedule.endLocalDateTime?.with(newDate)
            view.showEndDateText(schedule.endLocalDateTime!!.year, DateInfo(schedule.endLocalDateTime!!.monthValue, schedule.endLocalDateTime!!.dayOfMonth, schedule.endLocalDateTime!!.dayOfWeek.value))
        }
    }


    private fun getLocationCallback(context: Context): LocationCallback {
        return object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                val locationList = locationResult.locations
                if (locationList.isNotEmpty()) {
                    val location = locationList.last()
                    schedule.srcPosition = schedule.srcPosition ?: LatLng(location.latitude, location.longitude)
                    schedule.srcAddress =
                        geoCodingService.getAddress(schedule.srcPosition!!, context).toString()

                    val markerSnippet = "위도:${location.latitude} 경도:${location.longitude}"
                    Log.d("googleMap example", "onLocationResult : $markerSnippet")

                    if (schedule.srcPosition == null) {
                        schedule.srcPosition = LatLng(location.latitude, location.longitude)
                    }
                    updateMarker(location, schedule.srcAddress!!, true)
                }
            }
        }
    }

    fun calTimeRequired(){
        GoogleDistanceMatrixService.execute {
            val costRequired = if (schedule.meansType == MeansType.PUBLIC) {
                GoogleDistanceMatrixService.getTimeRequired(
                    schedule.srcPosition!!,
                    schedule.dstPosition!!,
                    schedule.startLocalDateTime!!,
                    schedule.meansType
                )
            }else{
                TMapDistanceMatrixService.getTimeRequired(
                    schedule.srcPosition!!,
                    schedule.dstPosition!!,
                    schedule.meansType)
            }
            schedule.cost = costRequired
            schedule.departureLocalDateTime = schedule.startLocalDateTime!!.minusSeconds(costRequired.value.toLong())
            view.showTimeRequired(costRequired.text, TimeInfo(schedule.departureLocalDateTime!!.hour, schedule.departureLocalDateTime!!.minute))
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