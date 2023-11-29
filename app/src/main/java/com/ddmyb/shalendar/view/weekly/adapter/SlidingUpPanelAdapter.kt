package com.ddmyb.shalendar.view.weekly.adapter

import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.util.NewScheduleDto
import com.ddmyb.shalendar.view.schedules.ScheduleActivity
import java.util.Calendar

class SlidingUpPanelAdapter(
    val scheduleList: ArrayList<ScheduleDto>,
    var selectedCal: Calendar,
    val context: Context
): RecyclerView.Adapter<SlidingUpPanelAdapter.ViewHolder>() {

    val TAG = "WeGlonD"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //itemView안에있는 뷰들 다 findViewById하고 val변수 만들자..
        val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
        val tv_start_time = itemView.findViewById<TextView>(R.id.tv_start_time)
        val tv_end_time = itemView.findViewById<TextView>(R.id.tv_end_time)
        val ll_schedule_color = itemView.findViewById<LinearLayout>(R.id.ll_schedule_color)
        val iv_alarm = itemView.findViewById<ImageView>(R.id.iv_alarm)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //itemview 레이아웃 inflate하고 ViewHolder 만들어서 return..
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_sliding_up, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return scheduleList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder안에 val변수로 있는 뷰들에 데이터 넣어주자..
        val schedule = scheduleList[position]

        val startCal = Calendar.getInstance()
        startCal.timeInMillis = schedule.startMills
        val startStr = calcDateString(startCal)

        val endCal = Calendar.getInstance()
        endCal.timeInMillis = schedule.endMills
        val endStr = calcDateString(endCal)

        holder.tv_name.text = schedule.title
        Log.d(TAG, "onBindViewHolder ${schedule.title}")

        if (schedule.startMills == 0L && schedule.endMills == 0L) {
            holder.tv_start_time.text = "하루종일"
            holder.tv_end_time.visibility = View.GONE
        }
        else {
            holder.tv_start_time.text = startStr
            holder.tv_end_time.text = endStr
        }

        if( true /* alarm set */ )
            holder.iv_alarm.visibility = View.VISIBLE
        else
            holder.iv_alarm.visibility = View.GONE

        val drawable = ContextCompat.getDrawable(context, R.drawable.round_boundry) as GradientDrawable
        drawable.setColor(ContextCompat.getColor(context,schedule.color))
        holder.ll_schedule_color.background = drawable

        holder.itemView.setOnClickListener {
            if (schedule.userId == UserRepository.getInstance()!!.getUserId()) {
                val intent = Intent(context, ScheduleActivity::class.java)
                intent.putExtra("NewSchedule", NewScheduleDto(schedule.scheduleId, 0L, schedule.groupId))
                context.startActivity(intent)
            }

        }
    }

    fun calcDateString(cal: Calendar) : String {
        if(cal.get(Calendar.YEAR) == selectedCal.get(Calendar.YEAR) && cal.get(Calendar.MONTH) == selectedCal.get(Calendar.MONTH) && cal.get(Calendar.DATE) == selectedCal.get(Calendar.DATE)) {
            val am_pm = when (cal.get(Calendar.AM_PM)) {
                Calendar.AM -> "AM"
                Calendar.PM -> "PM"
                else -> ""
            }
            return am_pm + " ${String.format("%02d",cal.get(Calendar.HOUR))}:${String.format("%02d",cal.get(Calendar.MINUTE))}"
        }
        else {
            return "${String.format("%02d", cal.get(Calendar.YEAR) % 100)}.${String.format("%02d",cal.get(Calendar.MONTH)+1)}.${String.format("%02d",cal.get(Calendar.DATE))}"
        }
    }
}