package com.ddmyb.shalendar.view.month.adapter.viewHolder

import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ItemMonthScheduleBinding
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto

class MonthCalendarDateScheduleViewHolder(
    val binding: ItemMonthScheduleBinding
): RecyclerView.ViewHolder(binding.root){

    fun bind(schedule: ScheduleDto) {
        binding.schedule = schedule
        val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.weekly_holiday) as GradientDrawable
//        drawable.mutate()
        drawable.setColor(ContextCompat.getColor(binding.root.context, schedule.color))
        binding.scheduleName.background = drawable
    }
}