package com.ddmyb.shalendar

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.ddmyb.shalendar.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private val firebaseRepository = FirebaseRepository.getInstance()

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            firebaseRepository!!.login(
                binding.etEmail.text.toString(),
                binding.etPwd.text.toString(),
                applicationContext,
                this
            )
        }

        binding.btnRegister.setOnClickListener { //회원가입 화면으로 이동
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}