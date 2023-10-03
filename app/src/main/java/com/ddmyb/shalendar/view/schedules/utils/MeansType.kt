package com.ddmyb.shalendar.view.schedules.utils

enum class MeansType {

    WALK, PUBLIC, CAR, BICYCLE, NULL;
    override fun toString(): String {
        return when (this) {
            WALK -> "walking"
            PUBLIC -> "transit"
            CAR -> "driving"
            BICYCLE -> "bicycling"
            NULL -> "NULL"
        }
    }
}