package com.ddmyb.shalendar.view.calendar_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.data.Calendar
import com.ddmyb.shalendar.databinding.FragmentCalendarListBinding
import com.ddmyb.shalendar.view.calendar_list.adapter.CalendarAdapter


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

        val origin = mutableListOf<Calendar>()

        origin.add(Calendar("Project"))
        origin.add(Calendar("개인 캘린더"))
        origin.add(Calendar("모바일 공학과"))

        binding.calendarlistRv.setHasFixedSize(true)
        binding.calendarlistRv.adapter = CalendarAdapter(origin)
        binding.calendarlistRv.layoutManager = LinearLayoutManager(activity)



        val searchViewTextListener: SearchView.OnQueryTextListener =
            object : SearchView.OnQueryTextListener {
                //검색버튼 입력시 호출, 검색버튼이 없으므로 사용하지 않음
                override fun onQueryTextSubmit(s: String): Boolean {
                    return false
                }

                //텍스트 입력/수정시에 호출
                override fun onQueryTextChange(s: String): Boolean {
                    search(s, origin, binding.calendarlistRv)
                    /*if (s.isNotEmpty() && s!="") {
                        search(s, origin, binding.calendarlistRv)
                        //Log.d("minseok", "SearchVies Text is changed : $s")
                    } else {
                        binding.calendarlistRv.adapter = CalendarAdapter(origin)
                    }*/
                    return false
                }
            }

        binding.fclSearchSv.setOnQueryTextListener(searchViewTextListener)
        return binding.root
    }

    private fun search(keyword: String, itemList: MutableList<Calendar>, rv: RecyclerView) {
        val searchresult = mutableListOf<Calendar>()
        for (item in itemList) {
            if (item.text.toLowerCase().contains(keyword.toLowerCase())) {
                searchresult.add(item)
            }
        }
        registerForContextMenu(rv)
        //rv.adapter.setItem
        rv.adapter = CalendarAdapter(searchresult)

    }

    override fun onResume() {
        super.onResume()
    }

}
