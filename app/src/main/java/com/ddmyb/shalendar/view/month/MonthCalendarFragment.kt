package com.ddmyb.shalendar.view.month

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.CalendarDate
import java.util.Calendar

class MonthCalendarFragment(val now: Long) : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_month_calendar, container, false)
    }

}