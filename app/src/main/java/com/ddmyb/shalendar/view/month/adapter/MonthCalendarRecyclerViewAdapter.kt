package com.ddmyb.shalendar.view.month.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.month.adapter.viewHolder.MonthCalendarDateScheduleViewHolder
import com.ddmyb.shalendar.view.month.adapter.viewHolder.MonthCalendarPageViewHolder

class MonthCalendarRecyclerViewAdapter(
    private val refList: List<Long>,
    private val activity: FragmentActivity
): RecyclerView.Adapter<MonthCalendarPageViewHolder>(), MonthCalendarAdapter {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthCalendarPageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_month_calendar_page, parent, false)
        return MonthCalendarPageViewHolder(DataBindingUtil.bind(view)!!, activity)
    }

    override fun getItemCount(): Int {
        return refList.size
    }

    override fun onBindViewHolder(holder: MonthCalendarPageViewHolder, position: Int) {
        holder.bind(refList[position])
    }
}