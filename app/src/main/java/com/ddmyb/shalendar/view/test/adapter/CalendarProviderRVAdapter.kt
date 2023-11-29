package com.ddmyb.shalendar.view.test.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ItemCalendarProviderRecyclerViewBinding
import com.ddmyb.shalendar.util.CalendarProvide
import com.ddmyb.shalendar.util.CalendarProvider
import com.ddmyb.shalendar.view.test.data.CalendarData

class CalendarProviderRVAdapter(
    val dataList: MutableList<CalendarProvide>
): RecyclerView.Adapter<CalendarProviderRVAdapter.VH>() {

    inner class VH(val binding: ItemCalendarProviderRecyclerViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CalendarProvide) {
            binding.data = data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_provider_recycler_view, parent, false)
        return VH(DataBindingUtil.bind(view)!!)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(dataList[position])
    }
}