package com.ddmyb.shalendar.view.alarm_manager

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityFullScreenAlarmBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


/**f
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class FullScreenAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenAlarmBinding
    private lateinit var vibrator: Vibrator
    private lateinit var mediaPlayer: MediaPlayer

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFullScreenAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            getSystemService(Vibrator::class.java)
        }else{
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(1000, 3000), 0))
        }else{
            vibrator.vibrate(longArrayOf(1000, 3000), 0)
        }

        mpCoroutine.start()

        turnScreenOnAndKeyguardOff()

        binding.btnAlarmCancel.setOnClickListener {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancelAll()
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

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private const val AUTO_HIDE = true

        /**
         * If [AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private const val AUTO_HIDE_DELAY_MILLIS = 3000

    }

    private val mpCoroutine = CoroutineScope(Dispatchers.IO).launch(start = CoroutineStart.LAZY) {
        mediaPlayer = MediaPlayer.create(this@FullScreenAlarmActivity, R.raw.demo)
        mediaPlayer.start()
        while (true) {
            if (mediaPlayer.isPlaying) { }
            else {
                mediaPlayer = MediaPlayer.create(this@FullScreenAlarmActivity, R.raw.demo)
                mediaPlayer.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        vibrator.cancel()

        mpCoroutine.cancel()
        if(mediaPlayer.isPlaying)
            mediaPlayer.stop()
        mediaPlayer.release()
    }
}