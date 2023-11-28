package com.ddmyb.shalendar.domain.setting.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ddmyb.shalendar.domain.Alarms.Alarm
import com.ddmyb.shalendar.domain.setting.Setting

@Dao
interface SettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(setting: Setting): Long
    @Update
    fun update(setting: Setting)
    @Query("DELETE FROM Setting WHERE id=:id")
    fun deleteById(id: Long)
    @Query("DELETE FROM Setting")
    fun deleteAll()
    @Query("SELECT * FROM Setting")
    fun getAll(): List<Setting>
}