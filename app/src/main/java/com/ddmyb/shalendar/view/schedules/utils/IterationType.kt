package com.ddmyb.shalendar.view.schedules.utils

enum class IterationType {
    NO_REPEAT, EVERY_DAY, EVERY_WEEK, EVERY_MONTH, EVERY_YEAR;

    override fun toString(): String {
        return when (this) {
            NO_REPEAT -> "반복 안 함"
            EVERY_DAY -> "매일"
            EVERY_WEEK -> "매주"
            EVERY_MONTH -> "매월"
            EVERY_YEAR -> "매년"
        }
    }
}
