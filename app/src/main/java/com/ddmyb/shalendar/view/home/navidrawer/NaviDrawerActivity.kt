package com.ddmyb.shalendar.view.home.navidrawer

import ToggleAnimation
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.view.home.navidrawer.adapter.OwnedCalendarAdapter

class NaviDrawerActivity :AppCompatActivity() {
    private val binding by lazy {
        NaviDrawerBinding.inflate(layoutInflater)
    }

    private val viewModel by lazy {
        ViewModelProvider(this)[NaviViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.loadAllCalendar{
            binding.ndTeamcalendarRv.apply{
                adapter = OwnedCalendarAdapter(viewModel.getList())
                layoutManager = LinearLayoutManager(
                    this@NaviDrawerActivity, LinearLayoutManager.VERTICAL, false)
                adapter!!.also{that ->
                    viewModel.ownedCalendarList.observeInsert {
                        that.notifyItemInserted(it)
                    }
                    viewModel.ownedCalendarList.observeRemove {
                        that.notifyItemRemoved(it)
                    }
                    viewModel.ownedCalendarList.observeChange {
                        that.notifyItemChanged(it)
                    }
                }
            }
        }
    }

    fun onClick(view: View) {
        var expandView: View? = null
        when (view) {
            binding.ndUpDownIv -> {
                expandView = binding.ndMycalendarLayout
            }
            binding.ndUpDown2Iv -> {
                expandView = binding.ndTeamcalendarRv
            }
        }
        if(expandView!!.visibility == View.VISIBLE) {
            ToggleAnimation.toggleArrow(view, true)
            ToggleAnimation.collapse(expandView)
        } else {
            ToggleAnimation.toggleArrow(view, false)
            ToggleAnimation.expand(expandView)
        }
    }

}