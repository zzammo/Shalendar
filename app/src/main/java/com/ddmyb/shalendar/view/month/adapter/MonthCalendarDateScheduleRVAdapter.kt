package com.ddmyb.shalendar.view.month.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.util.MutableLiveListData
import com.ddmyb.shalendar.view.month.adapter.viewHolder.MonthCalendarDateScheduleViewHolder
import com.ddmyb.shalendar.view.month.data.ScheduleData

class MonthCalendarDateScheduleRVAdapter(
    val scheduleList: MutableLiveListData<ScheduleData>
): RecyclerView.Adapter<MonthCalendarDateScheduleViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MonthCalendarDateScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_month_schedule, parent, false)
        return MonthCalendarDateScheduleViewHolder(DataBindingUtil.bind(view)!!)
    }

    override fun onBindViewHolder(holder: MonthCalendarDateScheduleViewHolder, position: Int) {
        holder.bind(scheduleList.value!![position])
    }

    override fun getItemCount(): Int {
        return scheduleList.value!!.size
    }
}