package com.ddmyb.shalendar.view.schedules.utils

enum class MeansType(val label: String) {
    WALK("pedestrian"),
    PUBLIC("transit"),
    CAR("car"),
    NULL("NULL");

    override fun toString(): String {
        return when (this) {
            WALK -> "pedestrian"
            PUBLIC -> "transit"
            CAR -> "car"
            NULL -> "NULL"
        }
    }
    companion object {
        fun fromLabel(label: String): MeansType? {
            return values().find { it.label == label }
        }
    }
}
