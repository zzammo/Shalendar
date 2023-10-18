package com.ddmyb.shalendar.view.holiday.data

import com.google.gson.annotations.SerializedName

object HolidayDTO
{
    data class Result(
        @SerializedName("response")
        val response: Response
    )
    data class Response(
        @SerializedName("body")
        val body: HolidayListItem,
    )

    data class HolidayListItem(
        @SerializedName("items")
        val items: HolidayItems,
        @SerializedName("totalCount")
        val totalCount: Int,

        )
    data class HolidayItems(
        @SerializedName("item")
        val item: List<HolidayItem>
    )
    data class HolidayItem(
        @SerializedName("dateName")
        val dateName: String,
        @SerializedName("locdate")
        val locdate: Int
    )
}