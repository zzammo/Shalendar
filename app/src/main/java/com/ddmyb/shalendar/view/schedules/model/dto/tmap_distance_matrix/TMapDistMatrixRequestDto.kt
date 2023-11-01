package com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix

import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils.LonLat
import java.io.Serializable


data class TMapDistMatrixRequestDto(
    val origins: Array<LonLat>,
    val destinations: Array<LonLat>,
    val transportMode: String
):Serializable
