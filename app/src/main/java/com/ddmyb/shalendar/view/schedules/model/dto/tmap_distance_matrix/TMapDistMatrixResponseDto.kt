package com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix

import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils.Destination
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils.MatrixRoute
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils.Meta
import com.ddmyb.shalendar.view.schedules.model.dto.tmap_distance_matrix.utils.Origin


data class TMapDistMatrixResponseDto(
    val meta: Meta,
    val origins: Array<Origin>,
    val destinations: Array<Destination>,
    val matrixRoutes: Array<MatrixRoute>
)
