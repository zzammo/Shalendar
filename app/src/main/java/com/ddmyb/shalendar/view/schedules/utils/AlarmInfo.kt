package com.ddmyb.shalendar.view.schedules.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.view.schedules.utils.AlarmInfo.AlarmType.*
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
class AlarmInfo {

    val alarmTypes = HashMap<AlarmType, Boolean>().apply {
        put(START_TIME, false)
        put(TEN_MIN_AGO, false)
        put(HOUR_AGO, false)
        put(DAY_AGO, false)
        put(CUSTOM, false)
    }

    private var customTimeDelta: TimeDelta? = TimeDelta()
    enum class AlarmType {
        START_TIME, TEN_MIN_AGO, HOUR_AGO, DAY_AGO, CUSTOM
    }

    fun toTimeDelta(): List<TimeDelta?> {
        val ret = mutableListOf<TimeDelta?>()
        alarmTypes.forEach { (alarmType, isEnabled) ->
            if (isEnabled) {
                val timeDelta = when (alarmType) {
                    START_TIME -> TimeDelta(0, 0, 0, 0)
                    TEN_MIN_AGO -> TimeDelta(0, 0, 0, 0)
                    HOUR_AGO -> TimeDelta(0, 0, 0, 0)
                    DAY_AGO -> TimeDelta(0, 0, 0, 0)
                    CUSTOM -> customTimeDelta
                }
                ret.add(timeDelta)
            }
        }
        return ret
    }

    fun toString(value: Int, index: Int): String {
        var ret = ""
        alarmTypes.forEach { (alarmType, isEnabled) ->
            if (isEnabled) {
                ret += when (alarmType) {
                    START_TIME -> "일정 시작시간, "
                    TEN_MIN_AGO -> "10분, "
                    HOUR_AGO -> "1시간, "
                    DAY_AGO -> "1일, "
                    CUSTOM -> getCustomText(value, index)
                }
            }
        }
        ret = if (ret == "") {
            "알람 설정 없음"
        } else {
            ret.substring(0, ret.length - 2) + " 전"
        }
        return ret
    }

    fun getCustomText(value: Int, index: Int): String {
        var ret = ""
        ret += value.toString()
        when (index) {
            0 -> ret += "분, "
            1 -> ret += "시간, "
            2 -> ret += "일, "
            3 -> ret += "주, "
        }
        return ret
    }

    fun updateCustomTime(value: Int, index: Int){
        customTimeDelta = when(index){
            0 -> TimeDelta(0 ,0 ,0, value)
            1 -> TimeDelta(0, 0, value,0)
            2 -> TimeDelta(0, value, 0,0)
            3 -> TimeDelta(value, 0, 0, 0)
            else -> null
        }
    }

    data class TimeDelta(
        var week: Int = 0,
        var day: Int = 0,
        var hour: Int = 0,
        var minute: Int = 0
    )
}
