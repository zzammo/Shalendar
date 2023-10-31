package com.ddmyb.shalendar.view.alarm_manager

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.Alarm
import com.ddmyb.shalendar.view.alarm_manager.adapter.AlarmRVAdapter

@RequiresApi(Build.VERSION_CODES.O)
class AlarmManagerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_alarm_manager, container, false)

        val rv = root.findViewById<RecyclerView>(R.id.rv_alarm)

        val itemList = ArrayList<Alarm>()

        itemList.add(Alarm("13:00","월급 두배로 받는법"))
        itemList.add(Alarm("11:00","학점 A+ 받는 법"))
        itemList.add(Alarm("10:00","구글 면접 질문에 대답하는 법"))
        itemList.add(Alarm("08:00","공부 잘하는 MBTI 순위"))

        val alarmRVAdapter = AlarmRVAdapter(itemList)
        alarmRVAdapter.notifyDataSetChanged()

        rv.adapter = alarmRVAdapter
        rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        return root
    }
}