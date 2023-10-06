package com.ddmyb.shalendar.background_service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class AlarmReceiver: BroadcastReceiver() {
    companion object{
        const val REQUEST_CODE = 101
    }
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.extras?.get("code") == REQUEST_CODE) {
            Toast.makeText(context, "Alarm Start", Toast.LENGTH_SHORT).show()
            var count = intent.getIntExtra("count", 0)
            Log.d("myLog", "$count")
        }
    }
}