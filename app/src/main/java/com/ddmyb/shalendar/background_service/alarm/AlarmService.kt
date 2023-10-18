package com.ddmyb.shalendar.background_service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId

class AlarmService {

    private val alarmManager: AlarmManager
    private val pendingIntent: PendingIntent
    constructor(context: Context){
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("code", AlarmReceiver.REQUEST_CODE)
        pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmReceiver.REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAlarmWithTime(seconds: Long){
        val instant = Instant.ofEpochSecond(seconds)

        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()

        Log.d("setAlarmWithTime",localDateTime.toString())
        
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            seconds * 1000,
            pendingIntent
        )
        Log.d("AlarmService", "Start")
    }
}