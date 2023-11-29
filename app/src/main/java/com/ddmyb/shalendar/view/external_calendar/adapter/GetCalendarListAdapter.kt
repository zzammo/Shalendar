package com.ddmyb.shalendar.view.external_calendar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ItemAnotherCalendarListBinding

class GetCalendarListAdapter (
    private val externalCalendar: MutableList<Pair<String, Boolean>>,
    private val beforeSetting: MutableList<String> = mutableListOf()
): RecyclerView.Adapter<GetCalendarListAdapter.MyViewHolder>(){

    private val selectedPositions: MutableList<Int> = mutableListOf()
    class MyViewHolder(val binding: ItemAnotherCalendarListBinding):RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemAnotherCalendarListBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_another_calendar_list, parent, false)))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.anotherAccountName.text = externalCalendar[position].first
        holder.binding.setCheckboxSwitch.isChecked = externalCalendar[position].second
        if(externalCalendar[position].second)selectedPositions.add(position)
            //beforeSetting.contains(externalCalendar[position])
        holder.binding.setCheckboxSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!selectedPositions.contains(position)) {
                    selectedPositions.add(position)
                }
            } else {
                selectedPositions.remove(position)
            }
        }
    }

    fun getSelectedPositions(): MutableList<Int> {
        return selectedPositions
    }
    override fun getItemCount(): Int {
        return externalCalendar.size
    }
}