package com.ddmyb.shalendar.view.home

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import java.util.logging.Logger

class NaviDrawerActivity :AppCompatActivity() {
    private val binding by lazy {
        NaviDrawerBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
            expandView.visibility = View.GONE
            view.animate().apply {
                duration = 300
                rotation(0f)
            }
        } else {
            expandView.visibility = View.VISIBLE
            view.animate().apply {
                duration = 300
                rotation(180f)
            }
        }
    }

}