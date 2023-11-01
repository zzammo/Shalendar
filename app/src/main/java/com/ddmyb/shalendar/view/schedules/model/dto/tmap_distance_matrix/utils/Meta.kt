package com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils

data class Meta(
    val status: String,
    val message: String,
    val moduleVersion: String,
    val mapVersion: String,
    val elapsedTime: Int,
    val realTimeTrafficDateTime: String,
    val patternTrafficDate: String,
    val customizationDateTime: String,
    val algorithm: String,
    val metric: String,
    val requestId: String,
    val gasStationPriceDateTime: String,
    val transportMode: String,
    val avoid: Array<Int>
)
