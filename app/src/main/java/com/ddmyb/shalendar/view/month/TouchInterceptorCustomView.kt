package com.ddmyb.shalendar.view.month

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout


class TouchInterceptorCustomView: LinearLayout {

    constructor(context: Context?): super(context)

    constructor(context: Context?, attrs: AttributeSet?): super(context, attrs)

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}