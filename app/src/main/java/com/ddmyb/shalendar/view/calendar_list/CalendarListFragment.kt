package com.ddmyb.shalendar.dummy_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.data.OwnedCalendar
import com.ddmyb.shalendar.databinding.FragmentCalendarListBinding
import com.ddmyb.shalendar.view.calendar_list.adapter.ExpandableListAdapter
import com.ddmyb.shalendar.view.calendar_list.adapter.ExpandableListAdapter.Companion.CHILD
import com.ddmyb.shalendar.view.calendar_list.adapter.ExpandableListAdapter.Companion.HEADER
import com.ddmyb.shalendar.view.calendar_list.adapter.SearchAdapter
import com.ddmyb.shalendar.view.dialog.CustomNewCalendarDialog


class CalendarListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentCalendarListBinding =
            FragmentCalendarListBinding.inflate(inflater)

        val origin = mutableListOf<OwnedCalendar>()

        origin.add(OwnedCalendar("개인 캘린더 (1)", HEADER))
        origin.add(OwnedCalendar("calendar 1", CHILD))
        //팀 캘린더 받기
        val teamCalendar = mutableListOf<OwnedCalendar>()
        teamCalendar.add(OwnedCalendar("Child 12", CHILD))
        teamCalendar.add(OwnedCalendar("멘석", CHILD))
        teamCalendar.add(OwnedCalendar("멘to석", CHILD))
        teamCalendar.add(OwnedCalendar("멘멘석석", CHILD))

        val cnt = teamCalendar.size
        origin.add(OwnedCalendar("팀 캘린더 ($cnt)", HEADER))
        origin.addAll(teamCalendar)
        var itemList: MutableList<OwnedCalendar> = origin.map { it.clone() }.toMutableList()
        val mAdapter = ExpandableListAdapter(itemList)
        binding.calendarlistRv.setHasFixedSize(true)
        binding.calendarlistRv.adapter = mAdapter
        itemList = origin.map { it.clone() }.toMutableList()

        val mLayoutManager = LinearLayoutManager(activity)
        binding.calendarlistRv.layoutManager = mLayoutManager

        val searchViewTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                //텍스트 입력/수정시에 호출
                override fun onQueryTextChange(s: String): Boolean {
                    if (s.isNotEmpty() && s!="") {
                        search(s, origin, binding.calendarlistRv)
                        //Log.d("minseok", "SearchVies Text is changed : $s")
                    } else {
                        binding.calendarlistRv.adapter = ExpandableListAdapter(itemList)
                        itemList = origin.map { it.clone() }.toMutableList()
                    }
                    return false
                }
            }

        binding.fclSearchSv.setOnQueryTextListener(searchViewTextListener)
        return binding.root
    }

    private fun search(keyword: String, itemList: MutableList<OwnedCalendar>, rv: RecyclerView) {
        val searchresult = mutableListOf<OwnedCalendar>()
        for (item in itemList) {
            if (item.type==CHILD && item.text.toLowerCase().contains(keyword.toLowerCase())) {
                searchresult.add(item)
            }
        }
        registerForContextMenu(rv)
        //rv.adapter.setItem
        rv.adapter = ExpandableListAdapter(searchresult)

    }

    override fun onResume() {
        super.onResume()
    }

}
