package com.ddmyb.shalendar.view.home.navidrawer.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.OwnedCalendar
import com.ddmyb.shalendar.databinding.ItemOwnedCalendarBinding

class OwnedCalendarAdapter (
    private val ownedCalendarList: MutableList<OwnedCalendar>
): RecyclerView.Adapter<OwnedCalendarAdapter.MyViewHolder>(){
    class MyViewHolder(val binding: ItemOwnedCalendarBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(ownedCalendar: OwnedCalendar) {
            binding.iocOwnerTv.text = ownedCalendar.text
            binding.iocCalendarIv.setImageResource(
                R.drawable.ic_face
            )
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemOwnedCalendarBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.item_owned_calendar,parent,false)))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(ownedCalendarList[position])
    }
    override fun getItemCount(): Int {
        return ownedCalendarList.size
    }
}