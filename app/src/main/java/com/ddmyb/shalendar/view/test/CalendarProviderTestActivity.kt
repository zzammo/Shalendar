package com.ddmyb.shalendar.view.test

import android.Manifest
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityCalendarProviderTestBinding
import com.ddmyb.shalendar.util.CalendarProvide
import com.ddmyb.shalendar.util.CalendarProvider
import com.ddmyb.shalendar.view.test.adapter.CalendarProviderRVAdapter
import com.ddmyb.shalendar.view.test.data.CalendarData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CalendarProviderTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarProviderTestBinding

    private val calendarList = mutableListOf<CalendarProvide>()
    private lateinit var adapter: CalendarProviderRVAdapter

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_calendar_provider_test)

        // Setup permission request launcher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result == true) {
                    Log.d("CalendarProviderTestActivity", "permission true")
                    CalendarProvider.getCalendarList(contentResolver) { map ->
                        CalendarProvider.getCalendars(contentResolver, map.keys.toList()) {
                            calendarList.add(it)
                            adapter.notifyItemInserted(calendarList.size-1)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Please allow this app to access your calendar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        binding.listRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@CalendarProviderTestActivity)
            this@CalendarProviderTestActivity.adapter = CalendarProviderRVAdapter(calendarList)
            this.adapter = this@CalendarProviderTestActivity.adapter
        }

        requestPermissionLauncher.launch(Manifest.permission.READ_CALENDAR)
    }

}