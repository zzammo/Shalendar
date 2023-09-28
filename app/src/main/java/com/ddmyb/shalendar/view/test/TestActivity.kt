package com.ddmyb.shalendar.view.test

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityTestBinding
import com.ddmyb.shalendar.view.month.MonthActivity

class TestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test)

        binding.monthCalendar.setOnClickListener {
            startActivity(
                Intent(this, MonthActivity::class.java)
            )
        }

        binding.dataBindingTestButton.setOnClickListener {
            val text = binding.dataBindingEditText.text.toString()
            binding.dataBindingTest = text
        }
    }
}