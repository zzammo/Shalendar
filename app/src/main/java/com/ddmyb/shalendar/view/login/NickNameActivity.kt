package com.ddmyb.shalendar.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityNickNameBinding
import com.ddmyb.shalendar.view.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class NickNameActivity : AppCompatActivity() {
    private var fbAuth: FirebaseAuth? = null //파이어베이스 인증 처리
    private var userRef: DatabaseReference? = null //실시간 데이터베이스

    private val binding by lazy {
        ActivityNickNameBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnRegister2.setOnClickListener(View.OnClickListener {
            fbAuth = FirebaseAuth.getInstance() // 파이어베이스 데이터베이스 연동
            userRef = FirebaseDatabase.getInstance().getReference("UserAccount")

            val strNickname = binding.etNickname.getText().toString()
            val strBirthdate = binding.etBirthdate.getText().toString()
            val strGender = binding.etGender.getText().toString()
            if (strNickname == null) {
                Toast.makeText(this@NickNameActivity, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                userRef!!.child(fbAuth!!.currentUser!!.uid).child("NickName").setValue(strNickname)
                if (strBirthdate != null) {
                    userRef!!.child(fbAuth!!.currentUser!!.uid).child("Birthdate").setValue(strBirthdate)
                }
                if (strGender != null) {
                    userRef!!.child(fbAuth!!.currentUser!!.uid).child("Gender").setValue(strGender)
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}