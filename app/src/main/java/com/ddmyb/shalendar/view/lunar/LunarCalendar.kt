package com.ddmyb.shalendar.view.lunar

import com.ibm.icu.util.ChineseCalendar
import java.util.Calendar
object LunarCalendar {
    fun LunarToSolar(yyyymmdd: String?): String {
        val cc = ChineseCalendar()
        val cal = Calendar.getInstance()
        if (yyyymmdd == null) return ""
        var date = yyyymmdd.trim { it <= ' ' }
        if (date.length != 8) {
            date =
                if (date.length == 4) date + "0101" else if (date.length == 6) date + "01" else if (date.length > 8) date.substring(
                    0,
                    8
                ) else return ""
        }
        cc[ChineseCalendar.EXTENDED_YEAR] = date.substring(0, 4).toInt() + 2637
        cc[ChineseCalendar.MONTH] = date.substring(4, 6).toInt() - 1
        cc[ChineseCalendar.DAY_OF_MONTH] = date.substring(6).toInt()
        cal.timeInMillis = cc.timeInMillis
        val y = cal[Calendar.YEAR]
        val m = cal[Calendar.MONTH] + 1
        val d = cal[Calendar.DAY_OF_MONTH]
        val ret = StringBuffer()
        ret.append(String.format("%04d", y))
        ret.append(String.format("%02d", m))
        ret.append(String.format("%02d", d))
        return ret.toString()
    }

    fun SolarToLunar(yyyymmdd: String?): String {
        val cc = ChineseCalendar()
        val cal = Calendar.getInstance()
        if (yyyymmdd == null) return ""
        var date = yyyymmdd.trim { it <= ' ' }
        if (date.length != 8) {
            date =
                if (date.length == 4) date + "0101" else if (date.length == 6) date + "01" else if (date.length > 8) date.substring(
                    0,
                    8
                ) else return ""
        }
        cal[Calendar.YEAR] = date.substring(0, 4).toInt()
        cal[Calendar.MONTH] = date.substring(4, 6).toInt() - 1
        cal[Calendar.DAY_OF_MONTH] = date.substring(6).toInt()
        cc.timeInMillis = cal.timeInMillis

        // ChinessCalendar.YEAR 는 1~60 까지의 값만 가지고 ,
        // ChinessCalendar.EXTENDED_YEAR 는 Calendar.YEAR 값과 2637 만큼의 차이를 가진다.
        val y = cc[ChineseCalendar.EXTENDED_YEAR] - 2637
        val m = cc[ChineseCalendar.MONTH] + 1
        val d = cc[ChineseCalendar.DAY_OF_MONTH]
        val ret = StringBuffer()
        if (y < 1000) ret.append("0") else if (y < 100) ret.append("00") else if (y < 10) ret.append(
            "000"
        )
        ret.append(y)
        if (m < 10) ret.append("0")
        ret.append(m)
        if (d < 10) ret.append("0")
        ret.append(d)
        return ret.toString()
    }
}