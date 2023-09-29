package com.ddmyb.shalendar.view.schedules

import android.os.Bundle
import android.view.View
import android.widget.TimePicker.OnTimeChangedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivitySchedulesBinding
import org.json.JSONObject

private lateinit var binding: ActivitySchedulesBinding

class SchedulesActivity : AppCompatActivity(), SchedulesContract.View{
    override fun showInfo(info: JSONObject) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedules)
        binding = ActivitySchedulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initTimeSelectionListener()
    }

    fun layoutGone(flag: Int) {
        if (flag == 1){
            binding.dateStartLayout.visibility = View.GONE
//            setOffClicked(0)
        }else if(flag == 2){
            binding.dateEndLayout.visibility = View.GONE
//            setOffClicked(2)
        }
    }


/**
//    var DatePickerFlag: Int = 0
//    var TimePickerFlag: Int = 0
//    val clicked = BooleanArray(4) { false }
//
//    fun initTimeSelectionListener() {
//        binding.startTimeTextview.setOnClickListener(View.OnClickListener {
//
//            when (DatePickerFlag) {
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                }
//                2 -> {
//                    binding.dateEndLayout.visibility = View.GONE
//                    setClicked(2, false)
//                }
//            }
//            DatePickerFlag = 0
//
//            when (TimePickerFlag) {
//                0 -> {
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(1, true)
//                    TimePickerFlag = 1
//                }
//                1 -> {
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(1, false)
//                    TimePickerFlag = 0
//                }
//                2 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(3, false)
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(1, true)
//                    TimePickerFlag = 1
//                }
//            }
//        })
//
//        binding.endTimeTextview.setOnClickListener(View.OnClickListener {
//            when (DatePickerFlag) {
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                }
//                2 -> {
//                    binding.dateEndLayout.visibility = View.GONE
//                    setClicked(2, false)
//                }
//            }
//            DatePickerFlag = 0
//
//            when (TimePickerFlag) {
//                0 -> {
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(3, true)
//                    TimePickerFlag = 2
//                }
//                1 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(1, false)
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(3, true)
//                    TimePickerFlag = 2
//                }
//                2 -> {
//                    binding.timeStartLayout.visibility = View.VISIBLE
//                    setClicked(3, false)
//                    TimePickerFlag = 0
//                }
//            }
//        })
//
//        binding.startDateTextview.setOnClickListener(View.OnClickListener {
//            when (DatePickerFlag) {
//                0 -> {
//                    binding.dateStartLayout.visibility = View.VISIBLE
//                    setClicked(0, true)
//                    DatePickerFlag = 1
//                }
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                    DatePickerFlag = 0
//                }
//                2 -> {
//                    binding.dateEndLayout.visibility = View.GONE
//                    setClicked(2, false)
//                    binding.dateStartLayout.visibility = View.VISIBLE
//                    setClicked(0, true)
//                    DatePickerFlag = 1
//                }
//            }
//
//            when (TimePickerFlag) {
//                1 -> {
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(1, false)
//                }
//                2 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(3, false)
//                }
//            }
//            TimePickerFlag = 0
//        })
//
//        binding.endDateTextview.setOnClickListener(View.OnClickListener {
//            when (DatePickerFlag) {
//                0 -> {
//                    binding.dateStartLayout.visibility = View.VISIBLE
//                    setClicked(2, true)
//                    DatePickerFlag = 2
//                }
//                1 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(0, false)
//                    binding.dateEndLayout.visibility = View.VISIBLE
//                    setClicked(2, true)
//                    DatePickerFlag = 2
//                }
//                2 -> {
//                    binding.dateStartLayout.visibility = View.GONE
//                    setClicked(2, false)
//                    DatePickerFlag = 0
//                }
//            }
//
//            when (TimePickerFlag) {
//                1 -> {
//                    binding.timeStartLayout.visibility = View.GONE
//                    setClicked(1, false)
//                }
//                2 -> {
//                    binding.timeEndLayout.visibility = View.GONE
//                    setClicked(3, false)
//                }
//            }
//            TimePickerFlag = 0
//        })
//    }
**/

    var DatePickerFlag: Int = 0
    var TimePickerFlag: Int = 0
    val clicked = BooleanArray(4) { false }

    fun initTimeSelectionListener() {
        binding.startTimeTextview.setOnClickListener {
            handleDateAndTimeClick(1, 2, 0, 1)
        }

        binding.endTimeTextview.setOnClickListener {
            handleDateAndTimeClick(1, 2, 3, 2)
        }

        binding.startDateTextview.setOnClickListener {
            handleDateAndTimeClick(0, 2, 1, 0)
        }

        binding.endDateTextview.setOnClickListener {
            handleDateAndTimeClick(2, 2, 2, 0)
        }
    }

    private fun handleDateAndTimeClick(dateStartVisibility: Int, dateEndVisibility: Int, timeVisibility: Int, newDatePickerFlag: Int) {
        if (DatePickerFlag == 1) {
            binding.dateStartLayout.visibility = View.GONE
            setClicked(0, false)
        } else if (DatePickerFlag == 2) {
            binding.dateEndLayout.visibility = View.GONE
            setClicked(2, false)
        }

        DatePickerFlag = newDatePickerFlag

        if (TimePickerFlag == 0) {
            binding.timeStartLayout.visibility = View.VISIBLE
            setClicked(1, true)
            TimePickerFlag = 1
        } else if (TimePickerFlag == 1) {
            binding.timeStartLayout.visibility = View.GONE
            setClicked(1, false)
            TimePickerFlag = 0
        } else if (TimePickerFlag == 2) {
            binding.timeEndLayout.visibility = View.GONE
            setClicked(3, false)
            binding.timeStartLayout.visibility = View.VISIBLE
            setClicked(1, true)
            TimePickerFlag = 1
        }

        binding.dateStartLayout.visibility = if (DatePickerFlag == 0) View.VISIBLE else View.GONE
        binding.dateEndLayout.visibility = if (DatePickerFlag == 2) View.VISIBLE else View.GONE
        binding.timeEndLayout.visibility = if (TimePickerFlag == 2) View.VISIBLE else View.GONE
        setClicked(dateStartVisibility, true)
        setClicked(dateEndVisibility, true)
        setClicked(timeVisibility, true)
    }

    private fun setClicked(i: Int, isClicked: Boolean) {
        clicked[i] = isClicked
        val textView = when (i) {
            0 -> binding.startDateTextview
            1 -> binding.startTimeTextview
            2 -> binding.endDateTextview
            3 -> binding.endTimeTextview
            else -> null
        }

        textView?.background = if (isClicked) {
            ContextCompat.getDrawable(this, R.drawable.ed_text)
        } else {
            ContextCompat.getDrawable(this, R.color.bg_white)
        }
    }


    fun initTimePicker(){
        binding.timeStartTimepicker.setOnTimeChangedListener(OnTimeChangedListener { timePicker, startHour, startMinute ->

            binding.startTimeTextview.text = getTimeText(startHour, startMinute)
            if (start_year == end_year && start_month == end_month && start_day == end_day) {
                if (startHour > end_hour) {
                    end_hour = startHour
                    end_minute = startMinute
                    end_time_textview.setText(getTimeText(end_hour, end_minute))
                    return@OnTimeChangedListener
                } else if (startHour == end_hour && startMinute > end_minute) {
                    end_hour = startHour
                    end_minute = startMinute
                    end_time_textview.setText(getTimeText(end_hour, end_minute))
                    return@OnTimeChangedListener
                }
            }
        })
    }

    private fun getTimeText(hour: Int, minute: Int): String? {
        var ret = ""
        ret += if (hour < 12) {
            "오전 $hour:"
        } else {
            "오후 " + (hour - 12).toString() + ":"
        }
        ret += if (minute < 10) {
            "0$minute"
        } else {
            minute.toString()
        }
        return ret
    }


}