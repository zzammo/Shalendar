package com.ddmyb.shalendar.view.calendar_list.adapter
import android.annotation.SuppressLint
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
            binding.calendarName.text = calendar.text
            binding.teammemberCnt.text = "5"
            binding.teammemberCntBorder.visibility = View.INVISIBLE
            binding.writeTime.text = "오전 9시 30분"
            binding.teammateName.text = "김민석, 양재모, 김동기, 이동원, 백명준"
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