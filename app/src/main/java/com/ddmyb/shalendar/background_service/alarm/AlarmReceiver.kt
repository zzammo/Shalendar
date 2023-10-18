package com.ddmyb.shalendar.background_service.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.alarm.FullScreenAlarmActivity
import com.ddmyb.shalendar.view.home.MainActivity


class AlarmReceiver: BroadcastReceiver() {
    companion object{
        const val REQUEST_CODE = 101
        const val CHANNEL_ID = "dori_mo"
    }

    lateinit var notificationManager: NotificationManager
    lateinit var powerManager: PowerManager
    lateinit var wakeLock: PowerManager.WakeLock

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.extras?.get("code") == REQUEST_CODE) {
            Toast.makeText(context, "Alarm Start", Toast.LENGTH_SHORT).show()
            Log.d("AlarmReceiver", "")

            notificationManager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            powerManager = context!!.getSystemService(Context.POWER_SERVICE) as PowerManager

            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP , "com.example.jetpacksemina.navigation:wakelock" )

            createNotificationChannel()
            deliverNotification(context)

        }
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                "shalendar", // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
                /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
                 */
            )
            notificationChannel.enableLights(true) // 불빛
//            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = "채널의 상세정보입니다." // 채널 정보
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    // Notification 등록
    @RequiresApi(Build.VERSION_CODES.O)
    private fun deliverNotification(context: Context) {
        val contentIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            REQUEST_CODE, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_IMMUTABLE
        )

        val fullscreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            action = "fullscreen_activity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val fullscreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullscreenIntent,
            PendingIntent.FLAG_IMMUTABLE
        )


        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable._742a24a_eb45_401f_92bb_826e9aac0ceb) // 아이콘
            .setContentTitle("SHALENDAR") // 제목
            .setContentText("출발 시간 입니다.") // 내용
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentIntent(fullscreenPendingIntent)
            .setFullScreenIntent(fullscreenPendingIntent, true)


//        wakeLock.acquire(5000)
        Log.d("notificationManager", "start")
        notificationManager.notify(REQUEST_CODE, builder.build())
    }

}