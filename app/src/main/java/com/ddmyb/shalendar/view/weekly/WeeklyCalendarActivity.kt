package com.ddmyb.shalendar.view.weekly

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.data.WeeklyDates
import com.ddmyb.shalendar.databinding.ActivityWeeklyCalendarBinding
import java.time.LocalDate

class WeeklyCalendarActivity : AppCompatActivity() {

    private var mBinding: ActivityWeeklyCalendarBinding? = null
    private val binding get() = mBinding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWeeklyCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val today = LocalDate.now()
        val dayofweek = today.dayOfWeek.value % 7
        val days = Array<LocalDate?>(7, {null})
        var curr = today
        for (i in dayofweek downTo 0) {
            days[i] = curr
            curr = curr.minusDays(1)
        }
        curr = today.plusDays(1)
        for (i in dayofweek + 1..6) {
            days[i] = curr
            curr = curr.plusDays(1)
        }

        val thisWeek = WeeklyDates(today.monthValue, days[0]!!.dayOfMonth, days[1]!!.dayOfMonth, days[2]!!.dayOfMonth,
            days[3]!!.dayOfMonth, days[4]!!.dayOfMonth, days[5]!!.dayOfMonth, days[6]!!.dayOfMonth)

        binding.data = thisWeek

    }
}