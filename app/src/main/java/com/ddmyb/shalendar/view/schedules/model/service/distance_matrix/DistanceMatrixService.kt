package com.ddmyb.shalendar.view.schedules.model.service.distance_matrix

import com.ddmyb.shalendar.view.schedules.model.data.google_distance_matrix.TimeRequiredResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DistanceMatrixService {
    @GET("json")
    fun getTimeRequired(@Query("destinations") destinations: String,
                        @Query("origins") origins: String,
                        @Query("key") key: String,
                        @Query("arrival_time") arrivalTime: String,
                        @Query("mode") mode: String,
                        @Query("language") language: String = "ko"): Call<TimeRequiredResponse>
}