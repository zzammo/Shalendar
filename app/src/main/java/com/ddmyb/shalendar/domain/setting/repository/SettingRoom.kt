package com.ddmyb.shalendar.domain.setting.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ddmyb.shalendar.domain.setting.Setting
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Setting::class], version = 1)
abstract class SettingRoom:RoomDatabase() {
    abstract fun settingDao(): SettingDao

    @OptIn(InternalCoroutinesApi::class)
    companion object{
        private var instance: SettingRoom? = null

        @Synchronized
        fun getInstance(context: Context): SettingRoom {
            if (instance == null){
                synchronized(RoomDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SettingRoom::class.java,
                        "setting-database1"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance!!
        }
    }
}