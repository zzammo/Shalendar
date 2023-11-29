package com.ddmyb.shalendar.view.calendar_list

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.data.Calendar
import com.ddmyb.shalendar.databinding.FragmentCalendarListBinding
import com.ddmyb.shalendar.domain.groups.repository.GroupRepository
import com.ddmyb.shalendar.view.calendar_list.adapter.CalendarAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class CalendarListFragment : Fragment() {
    private lateinit var groupNameTv: TextView
    private lateinit var binding: FragmentCalendarListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCalendarListBinding.inflate(inflater)
        val origin = mutableListOf<Calendar>(Calendar("개인 캘린더", mutableListOf<String>(),1,0,null))

        groupNameTv = requireActivity().findViewById<TextView>(R.id.tv_fragment_title)

        binding.calendarlistRv.setHasFixedSize(true)
        binding.calendarlistRv.adapter = CalendarAdapter(origin){
            groupNameTv.text = it
        }
        binding.calendarlistRv.layoutManager = LinearLayoutManager(activity)

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("로딩 중...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            progressDialog.dismiss()
        }

        CoroutineScope(Dispatchers.Main).launch{
            val groupList = GroupRepository().readUsersGroup()
            for (i in groupList){
                origin.add(Calendar(i.groupName, i.userId,i.memberCnt,i.latestUpdateMills,i.groupId))
            }
            progressDialog.dismiss()

            binding.calendarlistRv.adapter = CalendarAdapter(origin){
                groupNameTv.text = it
            }
        }
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
            if (item.Name.toLowerCase().contains(keyword.toLowerCase())) {
                searchresult.add(item)
            }
        }
        registerForContextMenu(rv)
        //rv.adapter.setItem
        rv.adapter = CalendarAdapter(searchresult) {
            groupNameTv.text = it
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun getGroup(){
        val origin = mutableListOf<Calendar>(Calendar("개인 캘린더", mutableListOf<String>(),1,0,null))
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("로딩 중...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
            progressDialog.dismiss()
        }

        CoroutineScope(Dispatchers.Main).launch{
            val groupList = GroupRepository().readUsersGroup()
            for (i in groupList){
                origin.add(Calendar(i.groupName, i.userId,i.memberCnt,i.latestUpdateMills,i.groupId))
            }
            progressDialog.dismiss()

            binding.calendarlistRv.adapter = CalendarAdapter(origin){
                groupNameTv.text = it
            }
            binding.calendarlistRv.adapter!!.notifyDataSetChanged()
        }
    }
    override fun onResume() {
        super.onResume()
    }
}
