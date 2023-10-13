package com.ddmyb.shalendar.util

interface HttpResult<T> {
    fun success(data : T)
    fun appFail()
    fun fail(throwable: Throwable)
    fun finally()
}