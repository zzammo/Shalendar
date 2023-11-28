package com.ddmyb.shalendar.view.setting

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.FragmentSettingBinding
import com.ddmyb.shalendar.domain.users.UserRepository
import com.ddmyb.shalendar.view.external_calendar.GetCalendarList
import com.ddmyb.shalendar.view.login.LoginActivity
import com.ddmyb.shalendar.view.login.SignUpActivity
import com.google.firebase.auth.FirebaseAuth

class SettingFragment : Fragment(R.layout.fragment_setting) {

    private lateinit var binding: FragmentSettingBinding
    private val userRepository = UserRepository.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSettingBinding.bind(view)


        binding.profileNameTextView.text = auth.currentUser!!.displayName
        binding.profileEmailTextView.text = auth.currentUser!!.email

        binding.logoutButton.setOnClickListener{
            userRepository?.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        binding.getAnotherCalendarLayout.setOnClickListener{
            val intent = Intent(requireContext(), GetCalendarList::class.java)
            startActivity(intent)
        }
    }
}