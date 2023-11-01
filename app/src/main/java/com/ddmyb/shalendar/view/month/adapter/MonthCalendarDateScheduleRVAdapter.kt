package com.ddmyb.shalendar.view.month.adapter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.ScheduleDto
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.adapter.viewHolder.MonthCalendarDateScheduleViewHolder
import com.ddmyb.shalendar.view.month.data.ScheduleData
import com.google.android.material.color.utilities.MaterialDynamicColors.background

class MonthCalendarDateScheduleRVAdapter(
    private val scheduleList: MutableLiveListData<ScheduleDto>
): RecyclerView.Adapter<MonthCalendarDateScheduleViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthCalendarDateScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month_schedule, parent, false)
        return MonthCalendarDateScheduleViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: MonthCalendarDateScheduleViewHolder, position: Int) {
        holder.bind(scheduleList.list[position])
    }

    override fun getItemCount(): Int {
        return scheduleList.list.size
    }
}