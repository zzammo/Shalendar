package com.ddmyb.shalendar.domain.Alarms.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ddmyb.shalendar.domain.Alarms.Alarm

@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: Alarm)
    @Update
    fun update(alarm: Alarm)
    @Query("DELETE FROM Alarm WHERE id=:id")
    fun deleteById(id: Int)
    @Query("DELETE FROM Alarm")
    fun deleteAll()
    @Query("SELECT * FROM Alarm")
    fun getAll(): List<Alarm>
}