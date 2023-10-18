package com.ddmyb.shalendar.util

interface HttpResult {
    fun success(data: Any?)
    fun appFail()
    fun fail(throwable: Throwable)
    fun finally()
}