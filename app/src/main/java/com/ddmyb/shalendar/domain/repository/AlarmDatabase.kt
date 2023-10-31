package com.ddmyb.shalendar.domain.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ddmyb.shalendar.domain.Alarm
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Alarm::class], version = 1)
abstract class AlarmDatabase:RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    @OptIn(InternalCoroutinesApi::class)
    companion object{
        private var instance: AlarmDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AlarmDatabase?{
            if (instance == null){
                synchronized(RoomDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmDatabase::class.java,
                        "alarm-database"
                    ).build()
                }
            }
            return instance
        }
    }
}