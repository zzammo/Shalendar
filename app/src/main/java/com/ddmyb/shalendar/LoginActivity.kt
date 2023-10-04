package com.ddmyb.shalendar

import android.content.Intent
import android.os.Bundle
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

class LoginActivity : AppCompatActivity() {
    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증 처리
    private var mDatabaseRef: DatabaseReference? = null //실시간 데이터베이스

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mFirebaseAuth = FirebaseAuth.getInstance()
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("MaengDol")
        binding.btnLogin.setOnClickListener {
            Toast.makeText(this@LoginActivity, "버튼 누름 확인", Toast.LENGTH_SHORT).show()
            val strEmail = binding.etEmail.getText().toString()
            val strPwd = binding.etPwd.getText().toString()
            mFirebaseAuth!!.signInWithEmailAndPassword(strEmail, strPwd)
                .addOnCompleteListener(this@LoginActivity) { task ->
                    if (task.isSuccessful) {
                        val intent = Intent(this@LoginActivity, NaviDrawerActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "로그인 실패..!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        binding.btnRegister.setOnClickListener { //회원가입 화면으로 이동
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}