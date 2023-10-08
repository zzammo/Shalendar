package com.ddmyb.shalendar.view.schedules.utils

enum class MeansType {

    WALK, PUBLIC, CAR, NULL;
    override fun toString(): String {
        return when (this) {
            WALK -> "pedestrian"
            PUBLIC -> "transit"
            CAR -> "car"
            NULL -> "NULL"
        }
    }
}