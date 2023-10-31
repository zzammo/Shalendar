package com.ddmyb.shalendar.background_service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.domain.Alarm
import com.ddmyb.shalendar.domain.repository.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmService {

    private val alarmManager: AlarmManager
    private val db: AlarmDatabase
    private val context: Context
    constructor(context: Context){
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        db = AlarmDatabase.getInstance(context)!!
        this.context = context
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAlarmWithTime(seconds: Long, newAlarm: Alarm){

        // db에 new alarm 저장
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().insert(newAlarm)
        }

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.apply {
            putExtra("code", newAlarm.id)
            putExtra("title", newAlarm.name)
            putExtra("memo", newAlarm.memo)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            newAlarm.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            seconds * 1000,
            pendingIntent
        )
        Log.d("AlarmService", "Start")
    }

    fun cancelAlarm(id: Int){
        val intent = Intent(context, AlarmReceiver::class.java)

        // db에서 저장된 알람 삭제
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().deleteById(id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }
}