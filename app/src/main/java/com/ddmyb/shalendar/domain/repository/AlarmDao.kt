package com.ddmyb.shalendar.domain.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ddmyb.shalendar.domain.Alarm

@Dao
interface AlarmDao {
    @Insert
    fun insert(alarm: Alarm)
    @Delete
    fun delete(alarm: Alarm)
    @Update
    fun update(alarm: Alarm)
    @Query("DELETE FROM Alarm WHERE id=:id")
    fun deleteById(id: Int)
    @Query("SELECT * FROM Alarm")
    fun getAll(): List<Alarm>
}