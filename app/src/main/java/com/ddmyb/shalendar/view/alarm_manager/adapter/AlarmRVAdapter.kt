package com.ddmyb.shalendar.view.alarm_manager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.Alarm
import com.ddmyb.shalendar.view.alarm_manager.adapter.viewHolder.AlarmViewHolder

class AlarmRVAdapter(val itemList: ArrayList<Alarm>): RecyclerView.Adapter<AlarmViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_schedule_sliding_up, parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.tv_name.text = itemList[position].name
        holder.tv_start_time.text = itemList[position].mills.toString()
        holder.tv_end_time.text = itemList[position].mills.toString()
    }
}