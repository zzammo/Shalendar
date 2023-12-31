package com.ddmyb.shalendar.background_service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.ddmyb.shalendar.domain.Alarms.Alarm
import com.ddmyb.shalendar.domain.Alarms.repository.AlarmDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmService {

    companion object{
        private var instance:AlarmService? = null
        @Synchronized
        fun getInstance(context: Context): AlarmService?{
            if (instance == null){
                synchronized(AlarmService::class) {
                    instance = AlarmService(context)
                }
            }
            return instance
        }
    }

    private val alarmManager: AlarmManager
    private val db: AlarmDatabase
    private val context: Context
    constructor(context: Context){
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        db = AlarmDatabase.getInstance(context)!!
        this.context = context
//        db.alarmDao().deleteAll()
//        //test db
//        val itemList = ArrayList<Alarm>()
//
//        itemList.add(Alarm("응용 프로그래밍","월급 두배로 받는법", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_0))
//        itemList.add(Alarm("운영체제","학점 A+ 받는 법", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_1))
//        itemList.add(Alarm("클라우드 컴퓨팅","구글 면접 질문에 대답하는 법", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_2))
//        itemList.add(Alarm("성격의 재발견","공부 잘하는 MBTI 순위", LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond() * 1000, R.color.cat_3))
//
//        itemList.forEach { item ->
//            db.alarmDao().insert(item)
//        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAlarmWithTime(newAlarm: Alarm){

        // db에 new alarm 저장
        val code = db!!.alarmDao().insert(newAlarm)

        val intent = Intent(context, AlarmReceiver::class.java)
        intent.apply {
            putExtra("code", code)
            putExtra("title", newAlarm.name)
            putExtra("memo", newAlarm.memo)
        }

        Log.d(this.toString(), "code: " + intent.extras?.getLong("code").toString() + " title: " + intent.extras!!
            .getString
            ("title"))

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            code.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            newAlarm.alarmMills,
            pendingIntent
        )
        Log.d("AlarmService", "Start")
    }

    fun cancelAlarm(id: Long){
        val intent = Intent(context, AlarmReceiver::class.java)

        // db에서 저장된 알람 삭제
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().deleteById(id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)
    }

    fun finish(id: Long){
        CoroutineScope(Dispatchers.IO).launch {
            db!!.alarmDao().deleteById(id)
        }
    }

    fun getAllAlarm(): List<Alarm>{
        return db.alarmDao().getAll()
    }
}