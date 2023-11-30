package com.ddmyb.shalendar.view.month.adapter.viewHolder

import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ItemMonthScheduleBinding
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import com.ddmyb.shalendar.domain.users.UserRepository

class MonthCalendarDateScheduleViewHolder(
    val binding: ItemMonthScheduleBinding
): RecyclerView.ViewHolder(binding.root){

    fun bind(schedule: ScheduleDto) {

        val drawable = ContextCompat.getDrawable(binding.root.context, R.drawable.weekly_holiday) as GradientDrawable
//        drawable.mutate()
        if (schedule.groupId == "" && schedule.userId != UserRepository.getInstance()!!.getUserId())
            drawable.setColor(ContextCompat.getColor(binding.root.context, R.color.line_gray))
        else
            drawable.setColor(ContextCompat.getColor(binding.root.context, schedule.color))

        binding.schedule = schedule

        binding.scheduleName.background = drawable
    }
}