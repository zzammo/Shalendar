package com.ddmyb.shalendar.view.month

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMonthBinding
import com.ddmyb.shalendar.util.Logger
import java.util.Calendar

class MonthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthBinding

    private lateinit var calendar: MonthCalendarFragment

    private val logger = Logger("MonthActivity", true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MonthActivity, R.layout.activity_month)

        calendar = MonthCalendarFragment(50)

        supportFragmentManager.commit {
            add(R.id.calendar, calendar)
            setReorderingAllowed(true)
        }
    }

}