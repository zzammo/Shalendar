package com.ddmyb.shalendar.background_service.alarm

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ddmyb.shalendar.R

class AlarmReceiver: BroadcastReceiver() {
    companion object{
        const val REQUEST_CODE = 101
    }
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.extras?.get("code") == REQUEST_CODE) {
            Toast.makeText(context, "Alarm Start", Toast.LENGTH_SHORT).show()
            var count = intent.getIntExtra("count", 0)
            Log.d("myLog", "$count")

            createNotificationChannel(context)

            // Create an explicit intent for an Activity in your app
            val newIntent = Intent(context, AlertDetails::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, newIntent,
                PendingIntent.FLAG_IMMUTABLE)

            var builder = NotificationCompat.Builder(context!!, "SHALENDAR")
                .setSmallIcon(R.drawable._742a24a_eb45_401f_92bb_826e9aac0ceb)
                .setContentTitle("SHALENDAR")
                .setContentText("출발 시간 입니다.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(context)) {
                // notificationId is a unique int for each notification that you must define
                val notificationId = 0
                notify(notificationId, builder.build())
            }

        }
    }

    private fun createNotificationChannel(context: Context?) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "SHALENDAR"
            val descriptionText = "description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("SHALENDAR", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}