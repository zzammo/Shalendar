package com.ddmyb.shalendar.view.schedules.distance.adapter

import android.util.Log
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.view.schedules.distance.model.TimeRequiredResponse
import com.ddmyb.shalendar.view.schedules.utils.MeansType
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import java.time.LocalDateTime

object RetrofitImpl {

    private const val URL = "https://maps.googleapis.com/maps/api/distancematrix/json"
    private const val API_KEY = BuildConfig.MAPS_API_KEY

    private val retrofit = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: RetrofitService = retrofit.create(RetrofitService::class.java)

    suspend fun getTimeRequired(dstLatLng: LatLng,
                        srcLatLng: LatLng,
                        dstTime: LocalDateTime,
                        meansType: MeansType): Int{
        val dst = dstLatLng.latitude.toString() + "%2C" + dstLatLng.longitude.toString()
        val src = srcLatLng.latitude.toString() + "%2C" + srcLatLng.longitude.toString()
        val timeRequiredResponse =
            service.getTimeRequired(dst, src, API_KEY, dstTime.toString(), meansType.toString())
                .execute().body()
        return timeRequiredResponse?.rows!![0].elements[0].duration.value
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
    fun execute(ifFail: suspend () -> Unit = {}, block: suspend RetrofitImpl.() -> Unit): Job {
        return CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "execute block start")
                block()
                Log.d(TAG, "execute block end")
            } catch (e : Exception) {
                Log.d(TAG, "execute exception\n${e.message}")
                ifFail()
            }
        }
    }
}