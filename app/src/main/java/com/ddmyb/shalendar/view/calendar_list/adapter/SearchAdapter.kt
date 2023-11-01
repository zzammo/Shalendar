package com.ddmyb.shalendar.view.calendar_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.OwnedCalendar
import com.ddmyb.shalendar.databinding.ItemOwnedCalendarChildBinding

class SearchAdapter (
    private var ownedCalendarList: MutableList<OwnedCalendar>
): RecyclerView.Adapter<SearchAdapter.MyViewHolder>(){
    class MyViewHolder(val binding: ItemOwnedCalendarChildBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(ownedCalendar: OwnedCalendar) {
            binding.iocChildTitle.text = ownedCalendar.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemOwnedCalendarChildBinding.bind(LayoutInflater.from(parent.context).inflate(
            R.layout.item_owned_calendar,parent,false)))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(ownedCalendarList[position])
    }

    override fun getItemCount(): Int {
        return ownedCalendarList.size
    }
}