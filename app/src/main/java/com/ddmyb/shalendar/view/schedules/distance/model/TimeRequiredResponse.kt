package com.ddmyb.shalendar.view.schedules.distance.model

import com.ddmyb.shalendar.view.schedules.distance.model.DistanceMatrixRow

data class TimeRequiredResponse(
    var destination_addresses: Array<String>,
    var origin_addresses: Array<String>,
    var rows: Array<DistanceMatrixRow>,
    var status: String,
    var error_message: String
)
