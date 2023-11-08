package com.ddmyb.shalendar.view.calendar_list.adapter
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.Calendar
import com.ddmyb.shalendar.databinding.ItemCalendarBinding
import com.ddmyb.shalendar.view.home.CalendarFragment
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class CalendarAdapter (
    private val ownedCalendarList: MutableList<Calendar>
): RecyclerView.Adapter<CalendarAdapter.MyViewHolder>(){
    class MyViewHolder(val binding: ItemCalendarBinding):RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n", "ResourceType")
        fun bind(calendar: Calendar) {
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
                binding.teammateName.text = ""
            }
            else{
                binding.teammemberCnt.text = calendar.cnt.toString()
                binding.teammemberCntBorder.visibility = View.INVISIBLE
                val changeDate = Instant.ofEpochMilli(calendar.time).atZone(ZoneId.systemDefault()).toLocalDateTime()
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
            itemView.setOnClickListener {
                val fragmentManager = (itemView.context as FragmentActivity).supportFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                val calendarFragment = CalendarFragment(calendar.groupId)
                fragmentTransaction.replace(R.id.main_frame, calendarFragment, "CalendarHostFragment")
                fragmentTransaction.addToBackStack(null) // 뒤로 가기 동작 지원
                fragmentTransaction.commit()
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