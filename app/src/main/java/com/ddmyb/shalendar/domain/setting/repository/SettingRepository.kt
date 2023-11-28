package com.ddmyb.shalendar.domain.setting.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ddmyb.shalendar.domain.Alarms.Alarm
import com.ddmyb.shalendar.domain.setting.Setting
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Setting::class], version = 1)
abstract class SettingRepository:RoomDatabase() {
    abstract fun settingDao(): SettingDao

    @OptIn(InternalCoroutinesApi::class)
    companion object{
        private var instance: SettingRepository? = null

        @Synchronized
        fun getInstance(context: Context): SettingRepository{
            if (instance == null){
                synchronized(RoomDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SettingRepository::class.java,
                        "setting-database1"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance!!
        }
    }
}