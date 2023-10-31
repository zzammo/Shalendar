package com.ddmyb.shalendar.background_service.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.alarm_manager.FullScreenAlarmActivity
import com.ddmyb.shalendar.view.home.MainActivity


class AlarmReceiver: BroadcastReceiver() {
    companion object{
        const val CHANNEL_ID = "channel"
        const val CHANNEL_NAME = "channel1"
    }

    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH)
        )

        builder = NotificationCompat.Builder(context, CHANNEL_ID)

        val fullscreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            action = "fullscreen_activity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val requestCode = intent?.extras!!.getInt("request_code")
        val title = intent.extras!!.getString("title")
        val memo = intent.extras!!.getString("memo")

        val fullscreenPendingIntent = PendingIntent.getActivity(
            context,
            requestCode,
            fullscreenIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = builder
            .setContentTitle(title)
            .setContentText(memo)
            .setSmallIcon(R.drawable._742a24a_eb45_401f_92bb_826e9aac0ceb)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setContentIntent(fullscreenPendingIntent)
            .setFullScreenIntent(fullscreenPendingIntent, true)
            .build()

        manager.notify(1, notification)
    }
}