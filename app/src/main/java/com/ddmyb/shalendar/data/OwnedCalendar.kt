package com.ddmyb.shalendar.data
class OwnedCalendar(var text: String, var type: Int) :Cloneable{
    var invisibleChildren: MutableList<OwnedCalendar>? = null
    public override fun clone(): OwnedCalendar {
        return OwnedCalendar(text, type)
    }
}
