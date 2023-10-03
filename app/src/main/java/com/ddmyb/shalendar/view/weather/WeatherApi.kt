package com.ddmyb.shalendar.view.weather

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.BuildConfig
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class WeatherApi (var context: Context) {
    private val key =  BuildConfig.Weather_API_KEY
    private val times = arrayOfNulls<LocalTime>(8)
    private val map = LamcParameter()
    private var nx = 0
    private var ny = 0
    private lateinit var today: LocalDate
    private lateinit var  now: LocalTime
    private lateinit var date: String
    private lateinit var time: String
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Throws(IOException::class)
    fun getWeather(weather: HashMap<LocalDate, HashMap<String, Int>>, lat: Double, lng: Double) {
        val i = settingTime()

        lamcproj(lat, lng, map)
        val xx = nx.toString()
        val yy = ny.toString()
        Log.d("minseok", "Position - lat : $lat lng : $lng -> nx : $xx ny : $yy")

        val urlBuilder = java.lang.StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst") /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + key)
            .appendUrlParam("pageNo", "1").appendUrlParam("numOfRows", "1500").appendUrlParam("dataType", "JSON")
            .appendUrlParam("base_date", date).appendUrlParam("base_time", time).appendUrlParam("nx", xx).appendUrlParam("ny", yy)
        val url = URL(urlBuilder.toString())
        Log.d("minseok",url.toString())

        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.setRequestProperty("Content-type", "application/json")
        Log.d("minseok", "Weather - Response code: " + conn.responseCode)

        val rd = if (conn.responseCode in 200..300) {
            BufferedReader(InputStreamReader(conn.inputStream))
        } else {
            BufferedReader(InputStreamReader(conn.errorStream))
        }

        val parser = JSONParser()
        val curr = times[i]!!.plusMinutes(50).format(DateTimeFormatter.ofPattern("HHmm"))
        try {
            val jsonObject = parser.parse(rd) as JSONObject
            val response = jsonObject["response"] as JSONObject
            val body = response["body"] as JSONObject
            val items = body["items"] as JSONObject
            val item = items["item"] as JSONArray

            val sky = HashMap<LocalDate, ArrayList<Int>>()

            for (json in item) {
                if (json is JSONObject) {
                    val weatherdate = LocalDate.parse(json["fcstDate"].toString(), DateTimeFormatter.ofPattern("yyyyMMdd"))
                    val type = json["category"] as String
                    val yebotime = json["fcstTime"] as String
                    if (yebotime == "0000") continue

                    weather.computeIfAbsent(weatherdate) { HashMap() }
                    sky.computeIfAbsent(weatherdate) { ArrayList() }

                    val data = weather[weatherdate]!!
                    when (type) {
                        "SKY" -> {
                            if (weatherdate == today && yebotime == curr) {
                                data["SKY"] = (json["fcstValue"] as String).toInt()
                            } else {
                                sky[weatherdate]!!.add((json["fcstValue"] as String).toInt())
                            }
                        }
                        "PTY" -> {
                            if (weatherdate == today && yebotime == curr) {
                                data["PTY"] = (json["fcstValue"] as String).toInt()
                            } else {
                                data["PTY"] = Integer.max(
                                    data.getOrDefault("PTY", 0),
                                    (json["fcstValue"] as String).toInt()
                                )
                            }
                        }
                        "TMP" -> {
                            if (weatherdate == today && yebotime == curr) {
                                data["TMP"] = (json["fcstValue"] as String).toInt()
                            }
                        }
                        "TMX" -> {
                            if (weatherdate != today) {
                                data["MAX"] = (json["fcstValue"] as String).toDouble().toInt()
                            }
                        }
                        "TMN" -> {
                            if (weatherdate != today) {
                                data["MIN"] = (json["fcstValue"] as String).toDouble().toInt()
                            }
                        }
                    }
                }
            }
            for (key in sky.keys) {
                if (key == today) continue
                val skies = sky[key]!!
                var sum = 0
                val size = skies.size
                for (s in skies) { sum += s }
                val avg = sum.toDouble() / size
                val res = if (avg > 3) 3 else if (avg > 2) 2 else 1
                //res가 1이면 맑음, 2면 구름, 3이면 흐림
                weather[key]!!["SKY"] = res
            }

            Log.d("minseok", weather.toString())
        } catch (e: ParseException) {
            e.printStackTrace()
            Log.d("minseok", "Weather Parse Failed")
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun settingTime(): Int{
        // 0200, 0500, 0800, 1100, 1400, 1700, 2000, 2300
        for (i in 0..7) {
            times[i] = LocalTime.of(2 + 3 * i, 10)
        }
        today = LocalDate.now()
        now = LocalTime.now()
        Log.d("minseok", "now init: " + now.format(DateTimeFormatter.ofPattern("HHmm")))
        if (now.isBefore(times[0])) {
            today = today.minusDays(1)
            now = LocalTime.of(23, 30)
        }
        for (i in 6 downTo 0) {
            if (now.isAfter(times[i])) {
                date = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                time = times[i]!!.minusMinutes(10).format(DateTimeFormatter.ofPattern("HHmm"))
                return i
            }
        }
        return 0
    }
    private fun StringBuilder.appendUrlParam(key: String, value: String): StringBuilder {
        append("&").append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8"))
        return this // StringBuilder를 반환하여 체이닝 가능하도록 함
    }
    inner class LamcParameter {
        var re = 6371.00877 /* 사용할 지구반경 [ km ] */
        var grid = 5.0 /* 격자간격 [ km ] */
        var slat1 = 30.0 /* 표준위도 [degree] */
        var slat2 = 60.0 /* 표준위도 [degree] */
        var olon = 126.0 /* 기준점의 경도 [degree] */
        var olat = 38.0 /* 기준점의 위도 [degree] */
        var xo = 210 / grid /* 기준점의 X좌표 [격자거리] */
        var yo = 675 / grid /* 기준점의 Y좌표 [격자거리] */
        var first = 0  /* 시작여부 (0 = 시작) */
    }
    private fun lamcproj(lat: Double, lng: Double, map: LamcParameter) {
        val pI = asin(1.0) * 2.0
        val degrad = pI / 180.0
        val re = map.re / map.grid
        val slat1 = map.slat1 * degrad
        val slat2= map.slat2 * degrad
        val olon= map.olon * degrad
        val olat= map.olat * degrad
        var sn= tan(pI * 0.25 + slat2 * 0.5) / tan(pI * 0.25 + slat1 * 0.5)
        sn = ln(cos(slat1) / cos(slat2)) / ln(sn)
        var sf= tan(pI * 0.25 + slat1 * 0.5)
        sf = sf.pow(sn) * cos(slat1) / sn
        var ro = tan(pI * 0.25 + olat * 0.5)
        ro = re * sf / ro.pow(sn)
        map.first = 1
        var ra = tan(pI * 0.25 + lat * degrad * 0.5)
        ra = re * sf / ra.pow(sn)
        var theta = lng * degrad - olon
        if (theta > pI) theta -= 2.0 * pI
        if (theta < -pI) theta += 2.0 * pI
        theta *= sn
        nx = ((ra * sin(theta)).toFloat() + map.xo + 1.5).toInt()
        ny = ((ro - ra * cos(theta)).toFloat() + map.yo + 1.5).toInt()
    }
}