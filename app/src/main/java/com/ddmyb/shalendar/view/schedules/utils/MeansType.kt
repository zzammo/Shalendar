package com.ddmyb.shalendar.view.schedules.utils

enum class MeansType {
    WALK,
    PUBLIC,
    CAR,
    NULL;

    override fun toString(): String {
        return when (this) {
            WALK -> "pedestrian"
            PUBLIC -> "transit"
            CAR -> "car"
            NULL -> "NULL"
        }
    }

    companion object {
        fun toMeansType(string: String): MeansType {
            return when (string) {
                "pedestrian" -> WALK
                "transit" -> PUBLIC
                "car" -> CAR
                else -> NULL
            }
        }
    }
}
