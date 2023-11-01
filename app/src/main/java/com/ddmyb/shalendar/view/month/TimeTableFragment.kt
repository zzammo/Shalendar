package com.ddmyb.shalendar.view.month

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentTimeTableBinding
import com.ddmyb.shalendar.databinding.ItemTimeTableScheduleBinding
import com.ddmyb.shalendar.util.CalendarFunc.ONE_DAY
import com.ddmyb.shalendar.view.month.data.TimeTableScheduleList

class TimeTableFragment(
    private val scheduleMap: List<TimeTableScheduleList>,
    private var idxHeight: Int = 50,
    private val idxWidthPercentage: Float = 0.7f
) : Fragment() {

    lateinit var binding: FragmentTimeTableBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimeTableBinding.inflate(inflater)

        idxHeight = (idxHeight * (context?.resources?.displayMetrics?.density?:100f)).toInt()

        val idxView = PercentageCustomView(requireContext())
        val ilp = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT)
        ilp.weight = idxWidthPercentage
        idxView.layoutParams = ilp

        val rlp = idxView.binding.container.layoutParams
        rlp.height = idxHeight*24
        idxView.binding.container.layoutParams = rlp

        idxView.binding.title.text = " "
        for (time in 0 until 24) {
            val textView = TextView(requireContext())
            textView.text = time.toString()
            textView.gravity = Gravity.CENTER_HORIZONTAL

            val tlp = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0)
            tlp.gravity = Gravity.CENTER
            textView.layoutParams = tlp

            idxView.addView(textView, time/24f, 1/24f)
//            idxView.addView(textView, time/24f)
//            idxView.binding.container.addView(textView)
        }

        binding.topLayout.addView(idxView)

        for (schedules in scheduleMap) {
            val colView = PercentageCustomView(requireContext())
            val clp = LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT)
            clp.weight = 1f
            colView.layoutParams = clp

            val crlp = colView.binding.container.layoutParams
            crlp.height = idxHeight*24
            colView.binding.container.layoutParams = crlp

            colView.binding.title.text = schedules.name
            for (schedule in schedules.list) {
                val scheduleBinding: ItemTimeTableScheduleBinding =
                    DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_time_table_schedule,
                        colView.binding.container,
                        false)

                scheduleBinding.data = schedule

                val start = schedule.startTime % ONE_DAY
                val end = schedule.endTime % ONE_DAY

                colView.addView(scheduleBinding.root, start/ONE_DAY, (end-start)/ONE_DAY)
//                colView.binding.container.addView(scheduleBinding.root)
            }

            binding.topLayout.addView(colView)
        }

        return binding.root
    }

}