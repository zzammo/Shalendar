package com.ddmyb.shalendar.view.schedules.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo.AlarmType.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.O)
class AlarmInfo {

    var alarmType = NULL
    private var customSeconds: Long = 0
    enum class AlarmType {
        START_TIME, TEN_MIN_AGO, HOUR_AGO, DAY_AGO, CUSTOM, NULL
    }

    fun toString(value: Int, index: Int): String {
        return when (alarmType) {
            NULL -> "알람 설정 없음"
            START_TIME -> "일정 시작 시각"
            TEN_MIN_AGO -> "10분 전"
            HOUR_AGO -> "1시간 전"
            DAY_AGO -> "1일 전"
            CUSTOM -> getCustomText(value, index)
        }
    }

    fun toSeconds():Long{
        return when (alarmType) {
            NULL -> 0
            START_TIME -> 0
            TEN_MIN_AGO -> LocalDateTime.of(0, 0, 0, 0, 10, 0).toEpochSecond(ZoneOffset.UTC)
            HOUR_AGO -> LocalDateTime.of(0, 0, 0, 1, 0, 0).toEpochSecond(ZoneOffset.UTC)
            DAY_AGO -> LocalDateTime.of(0, 0, 1, 0, 0, 0).toEpochSecond(ZoneOffset.UTC)
            CUSTOM -> customSeconds
        }
    }

    fun getCustomText(value: Int, index: Int): String {
        var ret = ""
        ret += value.toString()
        when (index) {
            0 -> ret += "분 전"
            1 -> ret += "시간 전"
            2 -> ret += "일 전"
            3 -> ret += "주 전"
        }
        return ret
    }

    fun updateCustomTime(value: Int, index: Int){
        val localDateTime =when(index) {
            0 -> LocalDateTime.of(0, 0, 0, 0, value, 0)
            1 -> LocalDateTime.of(0, 0, 0, value, 0, 0)
            2 -> LocalDateTime.of(0, 0, value, 0, 0, 0)
            3 -> LocalDateTime.of(0, 0, value * 7, 0, 0, 0)
            else -> LocalDateTime.of(0, 0, 0, 0, 0, 0)
        }
        customSeconds =  localDateTime.toEpochSecond(ZoneOffset.UTC)
    }
}
