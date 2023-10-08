package com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix

import com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils.DistanceMatrixRow

data class GoogleDistMatrixResponseDto(
    var destination_addresses: Array<String>,
    var origin_addresses: Array<String>,
    var rows: Array<DistanceMatrixRow>,
    var status: String,
    var error_message: String
)
