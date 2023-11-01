package com.ddmyb.shalendar.view.calendar_list.adapter
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.OwnedCalendar

class ExpandableListAdapter(private var ownedCalendarList: MutableList<OwnedCalendar>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data: MutableList<OwnedCalendar> = ownedCalendarList

    companion object {
        const val HEADER = 0
        const val CHILD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            HEADER -> {
                val headerView = inflater.inflate(R.layout.item_owned_calendar_header, parent, false)
                ListHeaderViewHolder(headerView)
            }
            CHILD -> {
                val childView = inflater.inflate(R.layout.item_owned_calendar_child, parent, false)
                ListChildViewHolder(childView)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = ownedCalendarList[position]
        when (item.type) {
            HEADER -> {
                val headerHolder = holder as ListHeaderViewHolder
                headerHolder.refferalItem = item
                headerHolder.bind(item)
                headerHolder.btnExpandToggle.setOnClickListener {
                    if (item.invisibleChildren == null) {
                        item.invisibleChildren = mutableListOf()
                        var count = 0
                        val pos: Int = data.indexOf(headerHolder.refferalItem)
                        while (data.size > pos + 1 && data[pos + 1].type == CHILD) {
                            item.invisibleChildren!!.add(data.removeAt(pos + 1))
                            count++
                        }
                        notifyItemRangeRemoved(pos + 1, count)
                        headerHolder.btnExpandToggle.setImageResource(R.drawable.ic_down)
                    } else {
                        val pos: Int = data.indexOf(headerHolder.refferalItem)
                        var index = pos + 1
                        for (i in item.invisibleChildren!!) {
                            data.add(index, i)
                            index++
                        }
                        notifyItemRangeInserted(pos + 1, index - pos - 1)
                        headerHolder.btnExpandToggle.setImageResource(R.drawable.ic_up)
                        item.invisibleChildren = null
                    }
                }
            }
            CHILD -> {
                val childHolder = holder as ListChildViewHolder
                childHolder.refferalItem = item
                childHolder.bind(item)
                childHolder.itemView.setOnClickListener {
                    // Handle item click here
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return ownedCalendarList[position].type
    }

    override fun getItemCount(): Int {
        return ownedCalendarList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(newItems: MutableList<OwnedCalendar>) {
        ownedCalendarList = newItems
        notifyDataSetChanged()
    }
    inner class ListHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val headerTitle: TextView = itemView.findViewById(R.id.header_title)
        val btnExpandToggle: ImageView = itemView.findViewById(R.id.btn_expand_toggle)
        var refferalItem: OwnedCalendar? = null

        fun bind(item: OwnedCalendar) {
            headerTitle.text = item.text
            if (item.invisibleChildren == null) {
                btnExpandToggle.setImageResource(R.drawable.ic_up)
            } else {
                btnExpandToggle.setImageResource(R.drawable.ic_down)
            }
        }
    }

    inner class ListChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val childTitle: TextView = itemView.findViewById(R.id.ioc_child_title)
        var refferalItem: OwnedCalendar? = null

        fun bind(item: OwnedCalendar) {
            childTitle.text = item.text
        }
    }
}
