package com.ddmyb.shalendar.background_service.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.view.alarm_manager.FullScreenAlarmActivity


class AlarmReceiver: BroadcastReceiver() {
    companion object{
        const val CHANNEL_ID = "channel"
        const val CHANNEL_NAME = "channel1"
    }

    private lateinit var manager: NotificationManager
    private lateinit var builder: NotificationCompat.Builder
    private lateinit var service: AlarmService

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        manager = context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val code = intent?.extras!!.getLong("code")
        val title = intent.extras!!.getString("title")
        val memo = intent.extras!!.getString("memo")


        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH)
        channel.enableVibration(false)
        channel.setSound(null, null)

        manager.createNotificationChannel(channel)

        builder = NotificationCompat.Builder(context, CHANNEL_ID)

        val fullscreenIntent = Intent(context, FullScreenAlarmActivity::class.java).apply {
            action = "fullscreen_activity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        Log.d(this.toString(), "code: $code title: $title")

        service = AlarmService.getInstance(context)!!
        service.finish(code)

        val fullscreenPendingIntent = PendingIntent.getActivity(
            context,
            code.toInt(),
            fullscreenIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = builder
            .setContentTitle(title)
            .setContentText(memo)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .setContentIntent(fullscreenPendingIntent)
            .setFullScreenIntent(fullscreenPendingIntent, true)
            .build()

        manager.notify(987654321, notification)

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.FULL_WAKE_LOCK or
                    PowerManager.ACQUIRE_CAUSES_WAKEUP or
                    PowerManager.ON_AFTER_RELEASE, "My:Tag"
        )
        wakeLock.acquire(5000)

    }
}