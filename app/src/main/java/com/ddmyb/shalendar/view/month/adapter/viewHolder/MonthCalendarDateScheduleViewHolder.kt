package com.ddmyb.shalendar.view.month.adapter.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.databinding.ItemMonthScheduleBinding
import com.ddmyb.shalendar.view.month.data.ScheduleData

class MonthCalendarDateScheduleViewHolder(
    val binding: ItemMonthScheduleBinding
): RecyclerView.ViewHolder(binding.root){

    fun bind(schedule: ScheduleData) {
        binding.schedule = schedule
    }
}