package com.ddmyb.shalendar.view.schedules

import org.json.JSONObject

interface SchedulesContract {
    interface View{
        fun showInfo(info : JSONObject)
    }
    interface Presenter {
        // onCreate 화면 초기화시에
        // 저장된 데이터가 있는지 Model 에서 확인하고
        // 확인한 결과에 따라 View 에 어떤 내용을 보일지 지시한다
        fun initInfo()

        // TextView 에 info 데이터를 보여주라고 View 에게 지시한다
        fun setInfo(info: JSONObject)

        // EditText 에 입력된 info 데이터를 저장하라고 Model 에게 지시한다
        fun saveInfo(info: JSONObject)
    }
}