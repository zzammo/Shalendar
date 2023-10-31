package com.ddmyb.shalendar.view.alarm_manager.adapter.viewHolder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R

class AlarmViewHolder(
    itemView: View
):RecyclerView.ViewHolder(itemView) {
    val tv_start_time = itemView.findViewById<TextView>(R.id.tv_start_time)
    val tv_end_time = itemView.findViewById<TextView>(R.id.tv_end_time)
    val ll_schedule_color = itemView.findViewById<LinearLayout>(R.id.ll_schedule_color)
    val tv_name = itemView.findViewById<TextView>(R.id.tv_name)
}