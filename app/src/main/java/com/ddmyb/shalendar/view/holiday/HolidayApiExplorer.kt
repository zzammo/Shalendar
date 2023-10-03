package com.ddmyb.shalendar.view.holiday

import android.content.Context
import android.util.Log
import android.util.Xml
import com.ddmyb.shalendar.BuildConfig
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Calendar

class HolidayApiExplorer {
    companion object {
        val TAG = "WeGlonD"
        @Throws(IOException::class, XmlPullParserException::class)
        fun getHolidays(
            Year: Int,
            Month: Int,
            namelist: ArrayList<String>,
            datelist: ArrayList<Long>
        ) {
            Log.d("WeGlonD", "getHolidays called")
            val key = BuildConfig.holiday_API_KEY
            val year = Integer.valueOf(Year).toString()
            var month = Integer.valueOf(Month).toString()
            if (Month < 10) month = "0$month"
            val urlBuilder =
                "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo" +
                        urlEncode("serviceKey", key, true) + urlEncode("pageNo","1") + urlEncode("numOfRows", "100") +
                        urlEncode("solYear", year) + if (Month > 0) urlEncode("solMonth", month) else ""

            Log.d(TAG, urlBuilder)
            val url = URL(urlBuilder)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Content-type", "application/json")
            Log.d("WeGlonD", "Holiday - Response code: " + conn.responseCode)
            val rd: BufferedReader
            rd = if (conn.responseCode >= 200 && conn.responseCode <= 300) {
                BufferedReader(InputStreamReader(conn.inputStream))
            } else {
                BufferedReader(InputStreamReader(conn.errorStream))
            }
            val parser = Xml.newPullParser()
            parser.setInput(rd)
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                var name: String? = null
                if (eventType == XmlPullParser.START_TAG) {
                    name = parser.name
                    if (name == "dateName") {
                        eventType = parser.next()
                        if (eventType == XmlPullParser.TEXT) {
                            namelist.add(parser.text)
                        }
                    } else if (name == "locdate") {
                        eventType = parser.next()
                        if (eventType == XmlPullParser.TEXT) {
                            datelist.add(YMDToMills(parser.text))
                        }
                    }
                }
                eventType = parser.next()
            }
            for (i in datelist.indices) {
                Log.d("WeGlonD", datelist[i].toString() + ' ' + namelist[i])
            }
            rd.close()
            conn.disconnect()
        }

        fun urlEncode(key: String, value: String, first: Boolean = false): String {
            return (if (first) "?" else "&") + URLEncoder.encode(key, "UTF-8") + "=" +
                    (if (first) value else URLEncoder.encode(value, "UTF-8"))
        }

        fun YMDToMills(ymd: String): Long {
            val int_ymd = ymd.toInt()
            val year = int_ymd / 10000
            val month = int_ymd % 10000 / 100
            val date = int_ymd % 100
            val cal = Calendar.getInstance()
            cal[year, month - 1, date, 0, 0] = 0
            cal[Calendar.MILLISECOND] = 0
            return cal.timeInMillis
        }
    }
}