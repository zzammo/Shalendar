package com.ddmyb.shalendar.view.holiday

import android.util.Log
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.util.HttpResult
import com.ddmyb.shalendar.view.holiday.data.HolidayApiService
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import com.ddmyb.shalendar.view.weather.data.WeatherDTO
import com.ddmyb.shalendar.view.weather.data.WeatherService
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class WeatherApi {
    companion object {
        private const val TAG = "minseok"
        private const val key = BuildConfig.holiday_API_KEY
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service: WeatherService = retrofit.create(WeatherService::class.java)
        fun getWeather(
            date : String,
            time : String,
            xx : String,
            yy : String,
            //httpResult: HttpResult
        ) {
            service.getWeather(key,1, 1500, "JSON", date, time, xx.toInt(), yy.toInt())
                .enqueue(object : Callback<WeatherDTO.WeatherResponse> {
                override fun onResponse(call: Call<WeatherDTO.WeatherResponse>, response: Response<WeatherDTO.WeatherResponse>) {
                    if (response.isSuccessful) {
                        val weatherResponse = response.body()
                        Log.d(TAG,weatherResponse.toString())
                        Log.d(TAG,response.toString())
                        if (weatherResponse != null) {
                            // 응답 처리
                            val items = weatherResponse.response.body.items.item
                            // ...
                            //httpResult.success(items)
                            Log.d(TAG,"1")
                        } else {
                            //httpResult.appFail()
                            Log.d(TAG,"1")
                        }
                    } else {
                        Log.d(TAG,"1")
                        //httpResult.appFail()
                    }
                    Log.d(TAG,"1")
                    //httpResult.finally()
                }

                override fun onFailure(call: Call<WeatherDTO.WeatherResponse>, t: Throwable) {
                    Log.d(TAG,"1")
                    //httpResult.fail(t)
                    //httpResult.finally()
                }
            })
        }
    }
}
