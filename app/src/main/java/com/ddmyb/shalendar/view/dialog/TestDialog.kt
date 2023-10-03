package com.ddmyb.shalendar.view.dialog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.AlertDialog
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityNewCalendarBinding
import com.ddmyb.shalendar.databinding.NaviDrawerTestPageBinding

class TestDialog : AppCompatActivity() {

    val binding by lazy {
        ActivityNewCalendarBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.dialogBtn.setOnClickListener {
            CustomNewCalendarDialog().show(supportFragmentManager, "")
        }
    }
}
