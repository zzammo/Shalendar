package com.ddmyb.shalendar.view.calendar_list.adapter
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.Calendar
import com.ddmyb.shalendar.databinding.ItemCalendarBinding
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class CalendarAdapter (
    private val ownedCalendarList: MutableList<Calendar>
): RecyclerView.Adapter<CalendarAdapter.MyViewHolder>(){
    class MyViewHolder(val binding: ItemCalendarBinding):RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(calendar: Calendar) {
            var changeDate:LocalDateTime = LocalDateTime.now()
            binding.calendarName.text = calendar.Name
            var teamMate: String = "팀원: "
            for(i in calendar.userIds){
                Log.d("oz","Adapter $i")
                teamMate += "$i "
            }
            binding.teammateName.text = teamMate
            if(calendar.Name=="개인 캘린더"){
                binding.teammemberCntBorder.visibility = View.VISIBLE
                binding.writeTime.text=""
                binding.teammemberCnt.text = "나"
            }
            else{
                binding.teammemberCnt.text = calendar.cnt.toString()
                binding.teammemberCntBorder.visibility = View.INVISIBLE
                changeDate = Instant.ofEpochMilli(calendar.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
                val duration: Duration = Duration.between(changeDate,LocalDateTime.now())
                var dpTime: String = ""
                if(duration.toDays()>0)
                    dpTime=duration.toDays().toString()+"일 전"
                else if(duration.toHours()>0)
                    dpTime=duration.toHours().toString()+"시간 전"
                else if(duration.toMinutes()>0)
                    dpTime=duration.toMinutes().toString()+"분 전"
                else
                    dpTime="방금"
                binding.writeTime.text = dpTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCalendarBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_calendar, parent, false)))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(ownedCalendarList[position])
    }

    override fun getItemCount(): Int {
        return ownedCalendarList.size
    }
}