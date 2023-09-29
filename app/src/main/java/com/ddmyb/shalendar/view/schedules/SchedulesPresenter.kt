package com.ddmyb.shalendar.view.schedules

class SchedulesPresenter(
    val view: SchedulesContract.View) : SchedulesContract.Presenter {

    override fun initInfo() {

    }

    override fun setInfo(info: JSONObject) {
        view.showInfo(info)
    }

    override fun saveInfo(info: JSONObject) {
        repository.saveInfo(info)
    }


}