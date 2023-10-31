package com.ddmyb.shalendar.util

import android.graphics.drawable.Drawable
import com.ddmyb.shalendar.R

enum class CatColor {
    cat_0, cat_1, cat_2, cat_3, cat_4, cat_5
    
    fun toColorId(): Int {
        return R.color.cat_0
    }
}