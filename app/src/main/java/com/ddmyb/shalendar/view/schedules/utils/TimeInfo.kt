package com.ddmyb.shalendar.view.schedules.utils

class TimeInfo(
    val hour: Int,
    val minute: Int
) {
    override fun toString(): String {
        val period = if (hour < 12) {
            if (hour == 0) "오전 12:" else "오전 $hour:"
        } else {
            if (hour == 12) "오후 12:" else "오후 ${hour - 12}:"
        }

        val minuteText = if (minute < 10) "0$minute" else minute.toString()

        return "$period$minuteText"
    }
}