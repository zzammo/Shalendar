package com.ddmyb.shalendar.view.month

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMonthBinding
import java.util.Calendar

class MonthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthBinding

    private lateinit var calendar: MonthCalendarFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MonthActivity, R.layout.activity_month)

        val refList = makeRefs(10)

        calendar = MonthCalendarFragment(refList)

        supportFragmentManager.commit {
            add(R.id.calendar, calendar)
            setReorderingAllowed(true)
        }
    }

    private fun makeRefs(count: Int): List<Long> {
        val list = mutableListOf<Long>()
        val cal = Calendar.getInstance()
        for (i in -count/2..count/2) {
            cal.add(Calendar.MONTH, i)
            list.add(cal.timeInMillis)
        }
        return list
    }

}