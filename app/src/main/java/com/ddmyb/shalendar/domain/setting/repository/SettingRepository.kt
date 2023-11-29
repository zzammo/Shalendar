package com.ddmyb.shalendar.domain.setting.repository

import android.content.Context
import com.ddmyb.shalendar.domain.setting.Setting

object SettingRepository {

    fun readSetting(context: Context): Setting {
        val db = SettingRoom.getInstance(context).settingDao()

        val setting: Setting
        if (db.getAll().isEmpty()) {
            setting = Setting()
            db.insert(setting)
        }
        else {
            setting = db.getAll()[0]
        }

        return setting
    }

    fun writeSetting(context: Context, setting: Setting) {
        val db = SettingRoom.getInstance(context).settingDao()

        db.update(setting)
    }

}