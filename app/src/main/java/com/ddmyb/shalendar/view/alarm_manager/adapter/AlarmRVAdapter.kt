package com.ddmyb.shalendar.view.alarm_manager.adapter

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.Alarms.Alarm
import com.ddmyb.shalendar.view.alarm_manager.adapter.viewHolder.AlarmViewHolder
import com.ddmyb.shalendar.view.schedules.utils.DateInfo
import com.ddmyb.shalendar.view.schedules.utils.TimeInfo
import java.time.Instant
import java.time.ZoneId

class AlarmRVAdapter(private val itemList: ArrayList<Alarm>): RecyclerView.Adapter<AlarmViewHolder>() {
    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val localDateTime = Instant.ofEpochMilli(itemList[position].mills).atZone(ZoneId.systemDefault()).toLocalDateTime()
        holder.tv_title_alarm_item.text = itemList[position].name
        holder.tv_date_start.text = DateInfo(localDateTime.monthValue, localDateTime.dayOfMonth, localDateTime.dayOfWeek.value).toString()
        holder.tv_time_start.text = TimeInfo(localDateTime.hour, localDateTime.minute).toString()
        val drawable = ContextCompat.getDrawable(context, R.drawable.round_boundry) as GradientDrawable
        drawable.setColor(ContextCompat.getColor(context, itemList[position].color))
        holder.ll_alarm_color.background = drawable
        val alarmLocalDateTime = Instant.ofEpochMilli(itemList[position].alarmMills).atZone(ZoneId.systemDefault()).toLocalDateTime()
        holder.tv_datetime_alarm.text = DateInfo(alarmLocalDateTime.monthValue, alarmLocalDateTime.dayOfMonth, alarmLocalDateTime
            .dayOfWeek.value).toString() + " " + TimeInfo(alarmLocalDateTime.hour, alarmLocalDateTime.minute)
                .toString() + " 알람"
    }

    fun addItem(position: Int, item: Alarm){
        itemList[position] = item
    }
    fun deleteItem(position: Int){
        itemList.removeAt(position)
    }
}