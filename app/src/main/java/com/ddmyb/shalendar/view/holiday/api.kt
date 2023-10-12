package com.ddmyb.shalendar.view.holiday

import android.util.Log
import com.ddmyb.shalendar.BuildConfig
import com.ddmyb.shalendar.view.holiday.data.HolidayApiService
import com.ddmyb.shalendar.view.holiday.data.HolidayDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class api {
    companion object {
        private const val TAG = "minseok"

        fun getHolidays(
            year: String,
            month: String,
            namelist: ArrayList<String>,
            datelist: ArrayList<Long>,

        ) {
            val key = BuildConfig.holiday_API_KEY

            // Create a Retrofit instance
            val retrofit = Retrofit.Builder()
                .baseUrl("http://apis.data.go.kr/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            // Create the API service
            val service = retrofit.create(HolidayApiService::class.java)

            // Make the API request
            val call = service.getHolidays(
                serviceKey = key,
                pageNo = 1,
                numOfRows = 100,
                solYear = year,
                solMonth = month,
                type = "json"
            )

            Log.d(TAG, "Request URL: ${call.request().url}")

            call.enqueue(object : Callback<HolidayDTO.Result> {
                override fun onResponse(call: Call<HolidayDTO.Result>, response: Response<HolidayDTO.Result>) {
                    if (response.isSuccessful) {
                        Log.d(TAG,"success"+response.body())
                        /*val holidayResponse = response.body()
                        holidayResponse?.items?.forEach { item ->
                            namelist.add(item.dateName)
                            datelist.add(YMDToMills(item.locdate))
                        }
                        for (i in datelist.indices) {
                            Log.d(TAG, datelist[i].toString() + ' ' + namelist[i])
                        }*/
                    } else {
                        // Handle the error here
                        Log.e(TAG, "Holiday - Request failed with code: " + response.code())
                    }
                }

                override fun onFailure(call: Call<HolidayDTO.Result>, t: Throwable) {
                    // Handle the error here
                    Log.e(TAG, "Holiday - Request failed", t)
                }
            })
        }

        // Rest of your functions remain the same
    }
}
