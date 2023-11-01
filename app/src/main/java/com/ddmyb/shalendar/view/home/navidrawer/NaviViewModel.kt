package com.ddmyb.shalendar.view.home.navidrawer

import androidx.lifecycle.ViewModel
import com.ddmyb.shalendar.data.OwnedCalendar
import com.ddmyb.shalendar.util.MutableLiveListData

val list = (1..10).map { OwnedCalendar(it.toString(),it.toInt()) }

class NaviViewModel: ViewModel() {
    val ownedCalendarList by lazy{
        MutableLiveListData<OwnedCalendar>()
    }

    fun getList(): MutableList<OwnedCalendar> {
        return ownedCalendarList.value!!
    }

    fun loadAllCalendar(after:() -> Unit = {}){
        //비동기 데베 받아오기
        ownedCalendarList.addAll(list)
        after()
    }

}