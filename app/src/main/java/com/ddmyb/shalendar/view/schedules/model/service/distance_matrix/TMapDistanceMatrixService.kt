package com.ddmyb.shalendar.view.schedules.model.service.distance_matrix

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.TMapDistMatrixRequestDto
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils.LonLat
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TMapDistanceMatrixService {
    private const val URL = "https://apis.openapi.sk.com/tmap/"
    private const val APP_KEY = BuildConfig.TMPAS_APP_KEY

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTimeRequired(
        dstLatLng: LatLng,
        srcLatLng: LatLng,
        meansType: MeansType
    ): TextValueObject {

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY // 요청 및 응답 본문을 로그로 출력

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // 로깅 인터셉터 추가
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service: DistanceMatrixApiClient = retrofit.create(DistanceMatrixApiClient::class.java)

        val dstLonLats = Array<LonLat>(1) { LonLat("", "") }
        dstLonLats[0] = LonLat(dstLatLng.longitude.toString(), dstLatLng.latitude.toString())
        val srcLonLats = Array<LonLat>(1) { LonLat("", "") }
        srcLonLats[0] = LonLat(srcLatLng.longitude.toString(), srcLatLng.latitude.toString())
        Log.d("dst", dstLonLats[0].toString())
        Log.d("src", srcLonLats[0].toString())
        Log.d("meansType", meansType.toString())

        val requestDto = TMapDistMatrixRequestDto(srcLonLats, dstLonLats, meansType.toString())
        val response =
            service.getTimeRequired(requestDto, APP_KEY)
                .execute()

        val responseBody = response.body()
        Log.d("response Code", response.code().toString())
        Log.d("response Body", response.body().toString())

        val seconds = responseBody!!.matrixRoutes[0].duration
        return TextValueObject(convertSecondsToKoreanTime(seconds), seconds)
    }

    private fun convertSecondsToKoreanTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        val hoursStr = if (hours > 0) "$hours 시 " else ""
        val minutesStr = if (minutes > 0) "$minutes 분 " else ""
        val secondsStr = "$remainingSeconds 초"

        return hoursStr + minutesStr + secondsStr
    }


    /**
     * @param ifFail Exception 발생시 실행될 함수
     * @param block execute 할 함수들을 넣는 block
     * @return Job
     *
     * @sample execute
     *
     * @see Job
     */

    val TAG = "RetroFitImpl"
    fun execute(ifFail: suspend () -> Unit = {}, block: suspend TMapDistanceMatrixService.() -> Unit): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "execute block start")
                block()
                Log.d(TAG, "execute block end")
            } catch (e : Exception) {
                Log.e(TAG, "execute exception\n${e.message}")
                ifFail()
            }
        }
    }
}