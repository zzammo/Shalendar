package com.ddmyb.shalendar.view.external_calendar

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.databinding.ActivityGetAnotherCalendarBinding
import com.ddmyb.shalendar.domain.setting.Setting
import com.ddmyb.shalendar.domain.setting.repository.SettingDao
import com.ddmyb.shalendar.domain.setting.repository.SettingRoom
import com.ddmyb.shalendar.util.CalendarProvider
import com.ddmyb.shalendar.view.external_calendar.adapter.GetCalendarListAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GetCalendarList : AppCompatActivity() {
    private lateinit var binding: ActivityGetAnotherCalendarBinding
    private lateinit var calendarList: MutableList<Pair<String, Boolean>>
    private var selectedList: MutableList<Int> = mutableListOf()
    private var beforeSetting: MutableList<String> = mutableListOf()
    private var accountCalendar = mutableMapOf<Int, String>()
    private lateinit var adapter: GetCalendarListAdapter
    private lateinit var db:SettingDao
    private lateinit var setting:Setting
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetAnotherCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = SettingRoom.getInstance(this@GetCalendarList).settingDao()
        if(db.getAll().isEmpty()){
            setting = Setting()
            db.insert(setting)
            Log.d("oz","New Calendars")
        }
        else{
            setting = db.getAll()[0]
            Log.d("oz","getting setting.Calendars ${db.getAll()[0].calendars}")

        }

        calendarList = mutableListOf()
        beforeSetting = mutableListOf()

        adapter = GetCalendarListAdapter(calendarList)
        binding.anotherCalendarRv.adapter = adapter

        binding.anotherCalendarRv.setHasFixedSize(true)
        binding.anotherCalendarRv.layoutManager = LinearLayoutManager(this)

        CalendarProvider.getCalendarList(this.contentResolver) { calendarMap ->
            accountCalendar = calendarMap.toMutableMap()

            for( i in accountCalendar){
                Log.d("oz GetCalendar", "${i.key} : ${i.value} idMap")
            }

            for ( i in calendarList){
                Log.d("oz GetCalendar", "${i.first} ${i.second} calendarList")
            }
            calendarList.clear()
            if(setting.calendars!=""){
                beforeSetting = setting.calendars.split('.').mapNotNull { accountCalendar[it.toInt()] }.toMutableList()
            }

            for(i in beforeSetting){
                val keyWithValues = accountCalendar.entries.find { it.value == i }?.key
                Log.d("oz GetCalendar","before setting ${i} ${keyWithValues}")
            }

            for (name in calendarMap.values) {
                calendarList.add(Pair(name, beforeSetting.contains(name)))
            }
            adapter.notifyDataSetChanged()
        }

        binding.getAnotherCalendarCheckBtn.setOnClickListener {
            selectedList = adapter.getSelectedPositions()
            val updatedSelectedList = selectedList.map { index ->
                val value = calendarList[index].first
                val keyWithValue = accountCalendar.entries.find { it.value == value }?.key
                keyWithValue ?: index // keyWithValue가 null이면 기존 index 유지
            }
            selectedList.clear()
            selectedList.addAll(updatedSelectedList)
            var string = ""
            for (i in selectedList){
                string += i.toString()
                string += "."
            }
            string = if (string.endsWith(".")) {
                string.substring(0, string.length - 1)
            } else {
                string
            }
            setting.calendars = string

            val progressBar = ProgressDialog(this@GetCalendarList)
            progressBar.setMessage("로딩 중...")
            progressBar.setCancelable(false)
            progressBar.show()

            CoroutineScope(Dispatchers.IO).launch {
                Log.d("oz","setting.Calendars${setting.calendars}")
                db.update(setting)
                Log.d("oz","settings${db.getAll()[0].calendars}")
                progressBar.dismiss()
                this@GetCalendarList.finish()
            }
        }
    }
}
