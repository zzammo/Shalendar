package com.ddmyb.shalendar.view.schedules.model.data.google_distance_matrix

data class TimeRequiredResponse(
    var destination_addresses: Array<String>,
    var origin_addresses: Array<String>,
    var rows: Array<DistanceMatrixRow>,
    var status: String,
    var error_message: String
)
