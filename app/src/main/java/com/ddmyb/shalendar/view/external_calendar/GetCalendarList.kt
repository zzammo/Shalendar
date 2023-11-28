package com.ddmyb.shalendar.view.external_calendar

import android.content.ContentResolver
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.databinding.ActivityGetAnotherCalendarBinding
import com.ddmyb.shalendar.util.CalendarProvider
import com.ddmyb.shalendar.view.external_calendar.adapter.GetCalendarListAdapter

class GetCalendarList : AppCompatActivity() {
    private lateinit var binding: ActivityGetAnotherCalendarBinding
    private lateinit var calendarList: MutableList<String>
    private var selectedList: MutableList<Int> = mutableListOf()
    private var idMap = mutableMapOf<Int, String>()
    private lateinit var adapter: GetCalendarListAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetAnotherCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendarList = mutableListOf()
        adapter = GetCalendarListAdapter(calendarList)
        binding.anotherCalendarRv.adapter = adapter

        binding.anotherCalendarRv.setHasFixedSize(true)
        binding.anotherCalendarRv.layoutManager = LinearLayoutManager(this)

        CalendarProvider.getCalendarList(this.contentResolver) { calendarMap ->
            idMap = calendarMap.toMutableMap()
            calendarList.clear()
            calendarList.addAll(calendarMap.values)
            adapter.notifyDataSetChanged()
        }

        binding.getAnotherCalendarCheckBtn.setOnClickListener {
            val updatedSelectedList = selectedList.map { index ->
                val value = calendarList[index]
                val keyWithValue = idMap.entries.find { it.value == value }?.key
                keyWithValue ?: index // keyWithValue가 null이면 기존 index 유지
            }
            selectedList.clear()
            selectedList.addAll(updatedSelectedList)
        }
    }
}
