package com.ddmyb.shalendar.background_service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.domain.Alarm
import com.ddmyb.shalendar.domain.repository.Alarm.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmService {

    private val alarmManager: AlarmManager
    private val db: AlarmDatabase
    private val context: Context
    constructor(context: Context){
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        db = AlarmDatabase.getInstance(context)!!
        this.context = context
        db.alarmDao().deleteAll()
        //test db
        val itemList = ArrayList<Alarm>()

        itemList.add(Alarm("응용 프로그래밍","월급 두배로 받는법", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_0))
        itemList.add(Alarm("운영체제","학점 A+ 받는 법", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_1))
        itemList.add(Alarm("클라우드 컴퓨팅","구글 면접 질문에 대답하는 법", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_2))
        itemList.add(Alarm("성격의 재발견","공부 잘하는 MBTI 순위", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_3))

        itemList.forEach { item ->
            db.alarmDao().insert(item)
        }
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

    fun getAllAlarm(): List<Alarm>{
        return db.alarmDao().getAll()
    }
}