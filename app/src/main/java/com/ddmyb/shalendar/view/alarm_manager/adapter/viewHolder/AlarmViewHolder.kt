package com.ddmyb.shalendar.view.alarm_manager.adapter.viewHolder

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R

class AlarmViewHolder(
    itemView: View
):RecyclerView.ViewHolder(itemView) {
    val tv_date_start = itemView.findViewById<TextView>(R.id.tv_date_start)
    val tv_time_start = itemView.findViewById<TextView>(R.id.tv_time_start)
    val ll_alarm_color = itemView.findViewById<LinearLayout>(R.id.ll_alarm_color)
    val tv_title_alarm_item = itemView.findViewById<TextView>(R.id.tv_title_alarm_item)
    val tv_datetime_alarm = itemView.findViewById<TextView>(R.id.tv_datetime_alarm)
}