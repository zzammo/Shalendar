package com.ddmyb.shalendar.view.schedules.distance.adapter

import com.ddmyb.shalendar.view.schedules.distance.model.TimeRequiredResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("json")
    fun getTimeRequired(@Query("destinations") destinations: String,
                        @Query("origins") origins: String,
                        @Query("key") key: String,
                        @Query("arrival_time") arrivalTime: String,
                        @Query("mode") mode: String): Call<TimeRequiredResponse>
}