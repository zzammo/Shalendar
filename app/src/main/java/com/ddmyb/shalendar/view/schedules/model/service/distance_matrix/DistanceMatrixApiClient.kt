package com.ddmyb.shalendar.view.schedules.model.service.distance_matrix

import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.GoogleDistMatrixResponseDto
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.TMapDistMatrixRequestDto
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.TMapDistMatrixResponseDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface DistanceMatrixApiClient {
    @GET("json")
    fun getTimeRequiredByPublic(@Query("destinations") destinations: String,
                                @Query("origins") origins: String,
                                @Query("key") key: String,
                                @Query("arrival_time") arrivalTime: String,
                                @Query("mode") mode: String = "transit",
                                @Query("language") language: String = "ko"): Call<GoogleDistMatrixResponseDto>

    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    @POST("matrix")
    fun getTimeRequired(
        @Body requestDto: TMapDistMatrixRequestDto,
        @Header("appKey") appKey: String,
        @Query("version") version: String = "1"): Call<TMapDistMatrixResponseDto>
}