package com.ddmyb.shalendar.view.login

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import com.ddmyb.shalendar.databinding.ActivityLoginBinding
import com.ddmyb.shalendar.domain.users.UserRepository


class LoginActivity : AppCompatActivity() {

    private val userRepository = UserRepository.getInstance()

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.btnLogin.setOnClickListener {
            userRepository!!.login(
                binding.etEmail.text.toString(),
                binding.etPwd.text.toString(),
                applicationContext
            ){
                this@LoginActivity.finish()
            }
        }

        binding.btnRegister.setOnClickListener { //회원가입 화면으로 이동
            val intent = Intent(this@LoginActivity, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}