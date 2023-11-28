package com.ddmyb.shalendar.view.setting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentSettingBinding
import com.ddmyb.shalendar.domain.setting.Setting
import com.ddmyb.shalendar.domain.setting.repository.SettingDao
import com.ddmyb.shalendar.domain.setting.repository.SettingRepository
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.view.external_calendar.GetCalendarList
import com.ddmyb.shalendar.view.login.LoginActivity
import com.ddmyb.shalendar.view.login.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private lateinit var binding: FragmentSettingBinding
    private val userRepository = UserRepository.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var setting: Setting
    private lateinit var db: SettingDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingBinding.bind(view)

        setting = Setting()
        db = SettingRepository.getInstance(requireContext()).settingDao()
        if(db.getAll().isEmpty()){
            setting = Setting()
            db.insert(setting)
            Log.d("oz","New Calendars")
        }
        else{
            setting = db.getAll()[0]
            Log.d("oz","getting setting.Calendars ${setting.calendars}")
        }

        binding.profileNameTextView.text = auth.currentUser!!.displayName
        binding.profileEmailTextView.text = auth.currentUser!!.email

        getSetting()

        binding.logoutButton.setOnClickListener{
            userRepository?.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.getAnotherCalendarLayout.setOnClickListener{
            val intent = Intent(requireContext(), GetCalendarList::class.java)
            startActivity(intent)
        }

        binding.setVibrateSwitch.setOnCheckedChangeListener { _, isChecked ->
            setting.vibration = isChecked
        }
        binding.setAutoAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            setting.alarm = isChecked
        }
        binding.setLunarSwitch.setOnCheckedChangeListener{ _, isChecked ->
            setting.lunar = isChecked
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("oz","SettingFragment OnPause")
        CoroutineScope(Dispatchers.IO).launch{
            setting = db.getAll()[0]
            db.update(setting)
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d("oz","SettingFragment OnResume")
        getSetting()
    }

    private fun getSetting(){
        if(setting.vibration){
            binding.setVibrateSwitch.isChecked = true
        }
        if(setting.alarm){
            binding.setAutoAlarmSwitch.isChecked = true
        }
        if(setting.lunar){
            binding.setLunarSwitch.isChecked = true
        }
    }
}