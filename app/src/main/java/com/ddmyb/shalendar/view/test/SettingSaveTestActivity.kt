package com.ddmyb.shalendar.view.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivitySettingSaveTestBinding
import com.ddmyb.shalendar.domain.setting.Setting
import com.ddmyb.shalendar.domain.setting.repository.SettingRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingSaveTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingSaveTestBinding

    private lateinit var db: SettingRoom
    private lateinit var setting: Setting

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_save_test)

        CoroutineScope(Dispatchers.IO).launch {
            db = SettingRoom.getInstance(applicationContext)

            if (db.settingDao().getAll().isEmpty()) {
                setting = Setting()
                db.settingDao().insert(setting)
            }
            else {
                setting = db.settingDao().getAll()[0]
                binding.settingTextView.text = setting.toString()
            }

            withContext(Dispatchers.Main) {
                binding.writeButton.setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        setting.calendars = binding.calendarEditText.text.toString()
                        db.settingDao().update(setting)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@SettingSaveTestActivity, "good", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}