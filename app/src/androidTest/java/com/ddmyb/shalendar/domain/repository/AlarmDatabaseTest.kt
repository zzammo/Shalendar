package com.ddmyb.shalendar.domain.repository

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before

class AlarmDatabaseTest{

    private lateinit var database: AlarmDatabase
    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AlarmDatabase::class.java).build()
    }

    @After
    fun closeDb() = database.close()
}