package com.ddmyb.shalendar.view.month

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityMonthBinding
import com.ddmyb.shalendar.databinding.ActivityMonthLibraryBinding
import com.ddmyb.shalendar.databinding.ItemMonthDateBinding
import com.ddmyb.shalendar.databinding.ItemMonthLibraryDayBinding
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.adapter.MonthCalendarDateScheduleRVAdapter
import com.ddmyb.shalendar.view.month.data.MonthPageData
import com.ddmyb.shalendar.view.month.data.ScheduleData
import com.ddmyb.shalendar.view.month.presenter.MonthCalendarPagePresenter
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

class MonthLibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMonthLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MonthLibraryActivity, R.layout.activity_month_library)

        supportFragmentManager.commit {
            add(R.id.calendar_fragment, MonthLibraryFragment(Calendar.getInstance()))
            setReorderingAllowed(true)
        }
    }


}