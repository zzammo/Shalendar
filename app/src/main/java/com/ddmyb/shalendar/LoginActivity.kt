package com.ddmyb.shalendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.*
import com.ddmyb.shalendar.databinding.ActivityLoginBinding
import com.ddmyb.shalendar.databinding.NaviDrawerBinding
import com.ddmyb.shalendar.view.home.navidrawer.NaviDrawerActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.kakao.sdk.common.util.Utility
import com.ddmyb.shalendar.manageSchedule

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            manageSchedule().Login(
                binding.etEmail.getText().toString(), binding.etPwd.getText().toString(), applicationContext
            )
        }
        binding.btnRegister.setOnClickListener { //회원가입 화면으로 이동
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}