package com.ddmyb.shalendar.view.holiday

import android.util.Log
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.holiday.data.HolidayApiService
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class HolidayApi {
    companion object {
        private const val TAG = "minseok"
        private const val key = BuildConfig.holiday_API_KEY
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: HolidayApiService = retrofit.create(HolidayApiService::class.java)
        fun getHolidays(
            year: Int,
            month: Int,
            httpResult: HttpResult<List<HolidayDTO.HolidayItem>>
        ) {
            Log.d(TAG, "HolidayApi - getHolidays $year.$month")
            service.getHolidays(key, 1, 100, year.toString(), String.format("%02d", month), "json")
                .enqueue(object : Callback<HolidayDTO.Result> {
                    override fun onResponse(call: Call<HolidayDTO.Result>, response: Response<HolidayDTO.Result>) {
                        Log.d(TAG, "getHolidays - onResponse")
                        if (response.isSuccessful) {
                            Log.d(TAG, "getHolidays - response isSuccessful")
                            val result = response.body()
                            Log.d(TAG, response.toString())
                            if (result != null) {
                                val holidayItems = result.response.body.items
                                val gson = Gson()
                                Log.d("minseok",holidayItems.toString())

                                val holidayList: List<HolidayDTO.HolidayItem> = when (holidayItems) {
                                    "" -> emptyList()
                                    else -> {
                                        val holidayListRealItem = gson.fromJson(gson.toJson(holidayItems),HolidayDTO.HolidayItems::class.java)
                                        when(result.response.body.totalCount) {
                                            0 -> emptyList()
                                            1 -> listOf(gson.fromJson(gson.toJson(holidayListRealItem.item), HolidayDTO.HolidayItem::class.java))
                                            else -> gson.fromJson(gson.toJson(holidayListRealItem.item), object : TypeToken<List<HolidayDTO.HolidayItem>>() {}.type)
                                        }
                                    }
                                }

                                httpResult.success(holidayList)
                            } else {
                                httpResult.appFail()
                            }
                        } else {
                            httpResult.appFail()
                        }
                        httpResult.finally()
                    }
                    override fun onFailure(call: Call<HolidayDTO.Result>, t: Throwable) {
                        Log.d(TAG, "getHolidays - onFailure")
                        httpResult.fail(t)
                        httpResult.finally()
                    }
                })
        }
    }
}
