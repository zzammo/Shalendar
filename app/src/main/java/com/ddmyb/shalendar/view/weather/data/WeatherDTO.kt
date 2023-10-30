package com.ddmyb.shalendar.view.weather.data
import com.google.gson.annotations.SerializedName
object WeatherDTO {
    data class WeatherResponse(
        val response: WeatherResponseBody
    )

    data class WeatherResponseBody(
        val body: WeatherBody
    )

    data class WeatherBody(
        val items: WeatherItems
    )

    data class WeatherItems(
        val item: List<WeatherItem>
    )

    data class WeatherItem(
        val fcstDate: String,
        val category: String,
        val fcstTime: String,
        val fcstValue: String
    )

}