package com.ddmyb.shalendar.view.weekly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityWeeklyCalendarBinding

class WeeklyCalendarActivity : AppCompatActivity() {

    lateinit var binding: ActivityWeeklyCalendarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeeklyCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        supportFragmentManager.commit {
            replace<WeeklyCalendarFragment>(R.id.frame_layout, "week")
            setReorderingAllowed(true)
//            addToBackStack("")
        }
    }
}