package com.ddmyb.shalendar.view.schedules.model.dto.google_distance_matrix.utils

data class TextValueObject(
    var text: String,
    var value: Int
){
    fun getTexts(): String{
        return this.text
    }
    fun getValues(): Int{
        return this.value
    }
}
