package com.ddmyb.shalendar.view.alarm_manager

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityFullScreenAlarmBinding
import com.ddmyb.shalendar.domain.setting.Setting
import com.ddmyb.shalendar.domain.setting.repository.SettingRepository
import com.ddmyb.shalendar.domain.setting.repository.SettingRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


/**f
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullScreenAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenAlarmBinding
    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var settingRoom: SettingRoom
    private lateinit var setting: Setting

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setting = SettingRepository.readSetting(this)

        Log.d("setting vibration", setting.vibration.toString())

        if (setting.vibration) {
            vbCoroutine.start()
        }else {
            mpCoroutine.start()
        }

        turnScreenOnAndKeyguardOff()

        binding.btnAlarmCancel.setOnClickListener {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()


            if (setting.vibration){
                vbCoroutine.cancel()
                vibrator.cancel()
            }else {
                mpCoroutine.cancel()
                if (mediaPlayer.isPlaying)
                    mediaPlayer.stop()
            }
            finish()
        }
    }

    private fun turnScreenOnAndKeyguardOff(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            Log.d("didwoah","version high")
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        } else {
            Log.d("didwoah","version low")
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED    // deprecated api 27
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD     // deprecated api 26
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON   // deprecated api 27
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON)
        }
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }

    private val mpCoroutine = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        mediaPlayer = MediaPlayer.create(this@FullScreenAlarmActivity, R.raw.demo)
        mediaPlayer.start()
        while (isActive) {
            if (mediaPlayer.isPlaying) { }
            else {
                mediaPlayer = MediaPlayer.create(this@FullScreenAlarmActivity, R.raw.demo)
                mediaPlayer.start()
            }
        }
    }

    private val vbCoroutine = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY){
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(Vibrator::class.java)
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // minSdk=27, VERSION_CODES.M=23. 늘 if문 충족
            val attributes = AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_ALARM).build()
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(2000, 1000), intArrayOf(255, 0), 0), attributes)
        } else {
            vibrator.vibrate(longArrayOf(1000, 2000), 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (setting.vibration) {
            vbCoroutine.cancel()
            vibrator.cancel()
        } else {
            mpCoroutine.cancel()
            if (mediaPlayer.isPlaying)
                mediaPlayer.stop()
            mediaPlayer.release()
        }
    }
}