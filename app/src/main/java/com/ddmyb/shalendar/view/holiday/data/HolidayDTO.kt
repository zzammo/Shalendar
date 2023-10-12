package com.ddmyb.shalendar.view.holiday.data

import com.google.gson.annotations.SerializedName

object HolidayDTO
{
    data class Result(
        @SerializedName("response")
        val response: Response
    )
    data class Response(
        @SerializedName("header")
        val header: HeaderItem,
        @SerializedName("body")
        val body: HolidayListItem,
    )

    data class HeaderItem(
        @SerializedName("resultCode")
        val resultCode: String,
        @SerializedName("resultMsg")
        val resultMsg: String,
    )
    data class HolidayListItem(
        @SerializedName("items")
        val items: HolidayItems,
        @SerializedName("numOfRows")
        val numOfRows: Int,
        @SerializedName("pageNo")
        val pageNo: Int,
        @SerializedName("totalCount")
        val totalCount: Int,

    )
    data class HolidayItems(
        @SerializedName("item")
        val item: List<HolidayItem>
    )
    data class HolidayItem(
        @SerializedName("dateKind")
        val dateKind: String,
        @SerializedName("dateName")
        val dateName: String,
        @SerializedName("isHoliday")
        val isHoliday: String,
        @SerializedName("locdate")
        val locdate: Int,
        @SerializedName("seq")
        val seq: Int
    )

}