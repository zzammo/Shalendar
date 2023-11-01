package com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils

data class DistanceMatrixElement(
    var status: String,
    var distance: TextValueObject,
    var duration: TextValueObject,
    var duration_in_traffic: TextValueObject,
    var fare: Fare
)
