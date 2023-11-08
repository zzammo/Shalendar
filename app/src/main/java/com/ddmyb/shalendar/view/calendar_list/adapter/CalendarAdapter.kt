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

class CalendarAdapter (
    private val ownedCalendarList: MutableList<Calendar>
): RecyclerView.Adapter<CalendarAdapter.MyViewHolder>(){
    class MyViewHolder(val binding: ItemCalendarBinding):RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(calendar: Calendar) {
            var teamMate: String = ""
            for(i in calendar.userIds){
                Log.d("oz","Adapter $i")
                teamMate += ""
            }
            binding.calendarName.text = calendar.Name
            binding.teammemberCnt.text = calendar.cnt.toString()
            binding.teammemberCntBorder.visibility = View.INVISIBLE
            binding.writeTime.text = calendar.time.toString()

            for(i in calendar.userIds){
                Log.d("oz","Adapter $i")
                teamMate += "$i "
            }
            binding.teammateName.text = teamMate
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