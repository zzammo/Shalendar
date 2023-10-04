package com.ddmyb.shalendar.view.schedules.utils

class DateInfo (
    val month: Int,
    val day: Int,
    val dayOfWeek: Int
){
    override fun toString(): String {
        val week = when (dayOfWeek) {
            1 -> "월"
            2 -> "화"
            3 -> "수"
            4 -> "목"
            5 -> "금"
            6 -> "토"
            7 -> "일"
            else -> ""
        }
        return "$month 월 $day 일 ($week)"
    }
}
