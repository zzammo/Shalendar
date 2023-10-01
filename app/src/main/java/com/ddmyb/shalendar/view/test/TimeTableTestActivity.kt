package com.ddmyb.shalendar.view.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityTimeTableTestBinding
import com.ddmyb.shalendar.view.month.TimeTableFragment
import com.ddmyb.shalendar.view.month.data.TimeTableScheduleData
import com.ddmyb.shalendar.view.month.data.TimeTableScheduleList

class TimeTableTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimeTableTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_time_table_test)

        val timetable = TimeTableFragment(
            listOf(TimeTableScheduleList(
                "name1",
                mutableListOf(
                    TimeTableScheduleData(
                        "test1",
                        (1000 * 60 * 60) * 7L,
                        (1000 * 60 * 60) * 10L
                    ),
                    TimeTableScheduleData(
                        "test2",
                        (1000 * 60 * 60) * 15L,
                        (1000 * 60 * 60) * 16L
                    ),
                    TimeTableScheduleData(
                        "test3",
                        (1000 * 60 * 60) * 18L,
                        (1000 * 60 * 60) * 19L
                    ),
                )
            ),
                TimeTableScheduleList(
                    "name2",
                    mutableListOf(
                        TimeTableScheduleData(
                            "test1",
                            (1000 * 60 * 60) * 7L,
                            (1000 * 60 * 60) * 10L
                        ),
                        TimeTableScheduleData(
                            "test2",
                            (1000 * 60 * 60) * 13L,
                            (1000 * 60 * 60) * 16L
                        ),
                        TimeTableScheduleData(
                            "test3",
                            (1000 * 60 * 60) * 18L,
                            (1000 * 60 * 60) * 23L
                        ),
                    )
                )
            )
        )

        supportFragmentManager.commit {
            add(R.id.timetable, timetable)
            setReorderingAllowed(true)
        }
    }
}