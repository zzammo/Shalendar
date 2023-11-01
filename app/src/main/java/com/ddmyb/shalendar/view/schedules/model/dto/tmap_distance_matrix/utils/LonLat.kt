package com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils

import java.io.Serializable

data class LonLat(
    val lon: String,
    val lat: String
):Serializable {
    override fun toString(): String {
        return "lon: $lon, lat: $lat"
    }
}
