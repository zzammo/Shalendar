package com.ddmyb.shalendar.data
class OwnedCalendar(var text: String, var type: Int) {
    var invisibleChildren: MutableList<OwnedCalendar>? = null
}
