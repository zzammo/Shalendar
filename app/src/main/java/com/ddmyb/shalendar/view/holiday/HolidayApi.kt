package com.ddmyb.shalendar.view.holiday

import android.util.Log
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.holiday.data.HolidayApiService
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
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
            year: String,
            month: String,
            httpResult: HttpResult<List<HolidayDTO.HolidayItem>>) {
            service.getHolidays(key, 1, 100, year, month, "json")
                .enqueue(object : Callback<HolidayDTO.Result>
                {
                    override fun onResponse(call: Call<HolidayDTO.Result>, response: Response<HolidayDTO.Result>) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            //Log.d(TAG,"success"+response.body())
                            if (result != null) {
                                Log.d(TAG, "response successful")
                                val holidayItems = result.response.body.items.item
                                holidayItems.let {
                                    for (holidayItem in it) {
                                        //Log.d(TAG, "dateName: ${holidayItem.dateName}, locdate: ${holidayItem.locdate}")
                                    }
                                }
                                httpResult.success(holidayItems)
                            } else {
                                Log.d(TAG, "response fail")
                                httpResult.appFail()
                            }
                        } else {
                            Log.e(TAG, "Holiday - Request failed with code: " + response.code())
                            httpResult.appFail()
                        }
                        httpResult.finally()
                    }
                    override fun onFailure(call: Call<HolidayDTO.Result>, t: Throwable) {
                        Log.e(TAG, "Holiday - Request failed", t)
                        httpResult.fail(t)
                        httpResult.finally()
                    }
                })
        }
    }
}
