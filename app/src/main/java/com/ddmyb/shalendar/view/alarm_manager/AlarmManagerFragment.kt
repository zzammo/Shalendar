package com.ddmyb.shalendar.view.alarm_manager

import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.background_service.alarm.AlarmService
import com.ddmyb.shalendar.domain.Alarms.Alarm
import com.ddmyb.shalendar.view.alarm_manager.adapter.AlarmRVAdapter
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


@RequiresApi(Build.VERSION_CODES.O)
class AlarmManagerFragment : Fragment() {

    lateinit var service:AlarmService
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        service = AlarmService(requireContext().applicationContext)
        val root = inflater.inflate(R.layout.fragment_alarm_manager, container, false)
        val rv = root.findViewById<RecyclerView>(R.id.rv_alarm)
        val itemList = service.getAllAlarm()
        val alarmRVAdapter = AlarmRVAdapter(itemList as ArrayList<Alarm>)
        alarmRVAdapter.notifyDataSetChanged()

        rv.adapter = alarmRVAdapter
        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                @NonNull recyclerView: RecyclerView,
                @NonNull viewHolder: RecyclerView.ViewHolder,
                @NonNull target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(@NonNull viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        //삭제할 아이템 담아두기
                        val deleteItem: Alarm = itemList[position]

                        //삭제 기능
                        service.cancelAlarm(deleteItem.id)
                        itemList.removeAt(position)
                        alarmRVAdapter.notifyItemRemoved(position)

                        //복구 기능
//                        Snackbar.make(rv, deleteItem.name, Snackbar.LENGTH_LONG)
//                            .setAction("복구", View.OnClickListener {
//                                itemList.add(position, deleteItem)
//                                alarmRVAdapter.notifyItemInserted(position)
//                            }).show()
                    }
                }
            }

            override fun onChildDraw(
                @NonNull c: Canvas,
                @NonNull recyclerView: RecyclerView,
                @NonNull viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c, recyclerView, viewHolder,
                    dX, dY, actionState, isCurrentlyActive
                )
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .addSwipeLeftLabel("삭제")
                    .setSwipeLeftLabelColor(R.color.white)
                    .setSwipeLeftActionIconTint(R.color.red)
                    .addBackgroundColor(R.color.red)
                    .addSwipeLeftBackgroundColor(R.color.red)
                    .addSwipeRightBackgroundColor(R.color.red)
                    .setSwipeRightLabelColor(R.color.red)
                    .setActionIconTint(R.color.red)
                    .create()
                    .decorate()
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }
        }).attachToRecyclerView(rv)
        return root
    }
}