package com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils

data class MatrixRoute(
    val status: String,
    val originIndex: Int,
    val destinationIndex: Int,
    val cost: Int,
    val duration: Int,
    val distance: Double,
    val toll: Boolean
)
