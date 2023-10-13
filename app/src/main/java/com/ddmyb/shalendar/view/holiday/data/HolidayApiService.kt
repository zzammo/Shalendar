package com.ddmyb.shalendar.view.holiday.data

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query

interface HolidayApiService {
    @GET("B090041/openapi/service/SpcdeInfoService/getHoliDeInfo")
    fun getHolidays(
        @Query("serviceKey") serviceKey: String,
        @Query("pageNo") pageNo: Int,
        @Query("numOfRows") numOfRows: Int,
        @Query("solYear") solYear: String,
        @Query("solMonth") solMonth: String,
        @Query("_type") type: String
    ): Call<HolidayDTO.Result>
}
