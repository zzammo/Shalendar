package com.ddmyb.shalendar.domain.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ddmyb.shalendar.domain.Alarm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

@Database(entities = [Alarm::class], version = 1)
abstract class AlarmDatabase:RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    @OptIn(InternalCoroutinesApi::class)
    companion object{
        private var instance: AlarmDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Alarm ADD COLUMN isFinished INTEGER DEFAULT 0 NOT NULL")
            }
        }

        @Synchronized
        fun getInstance(context: Context): AlarmDatabase?{
            if (instance == null){
                synchronized(RoomDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlarmDatabase::class.java,
                        "alarm-database"
                    ).addMigrations(MIGRATION_1_2)
                        .allowMainThreadQueries()
                        .build()
                }
            }
            return instance
        }
    }
}