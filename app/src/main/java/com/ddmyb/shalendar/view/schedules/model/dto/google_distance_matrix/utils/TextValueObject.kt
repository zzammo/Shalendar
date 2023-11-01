package com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils

data class TextValueObject(
    var text: String,
    var value: Int
){
    fun getText(): String{
        return this.text
    }
    fun getValue(): Int{
        return this.value
    }
}
