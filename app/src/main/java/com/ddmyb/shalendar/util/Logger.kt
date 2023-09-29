package com.ddmyb.shalendar.util

import android.util.Log

class Logger(private val tag: String, var enable: Boolean) {

    fun logD(message: String) {
        if (enable)
            Log.d(tag, message)
    }
}