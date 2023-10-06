package com.ddmyb.shalendar.view.schedules.model.service.distance_matrix

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.TextValueObject
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.time.ZoneOffset

object GoogleDistanceMatrixService {
    private const val URL = "https://maps.googleapis.com/maps/api/distancematrix/"
    private const val API_KEY = BuildConfig.MAPS_API_KEY

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val service: DistanceMatrixApiClient = retrofit.create(DistanceMatrixApiClient::class.java)
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getTimeRequired(dstLatLng: LatLng,
                                srcLatLng: LatLng,
                                dstTime: LocalDateTime,
                                meansType: MeansType): TextValueObject {
        val dst = dstLatLng.latitude.toString() + "," + dstLatLng.longitude.toString()
        val src = srcLatLng.latitude.toString() + "," + srcLatLng.longitude.toString()
        Log.d("dst", dst)
        Log.d("src", src)
        Log.d("meansType", meansType.toString())
        Log.d("dstTime", dstTime.toEpochSecond(ZoneOffset.UTC).toString())
        val response =
            service.getTimeRequiredByPublic(dst, src, API_KEY, dstTime.toEpochSecond(ZoneOffset.UTC).toString(), meansType.toString())
                .execute()
        val timeRequiredResponse = response.body()
        Log.d("response Code", response.code().toString())
        Log.d("response Body", response.body().toString())
        return timeRequiredResponse?.rows!![0].elements[0].duration
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
    fun execute(ifFail: suspend () -> Unit = {}, block: suspend GoogleDistanceMatrixService.() -> Unit): Job {
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