package com.ddmyb.shalendar.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityChangePwdBinding
import com.ddmyb.shalendar.domain.users.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChangePwdActivity : AppCompatActivity() {
    private var fbAuth: FirebaseAuth? = null //파이어베이스 인증 처리
    private var userRef: DatabaseReference? = null //실시간 데이터베이스

    private val binding by lazy {
        ActivityChangePwdBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fbAuth = FirebaseAuth.getInstance() // 파이어베이스 데이터베이스 연동
        userRef = FirebaseDatabase.getInstance().getReference("UserAccount")

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            //회원가입 처리 시작
            val curPwd = binding.etCurpwd.text.toString()
            val newPwd = binding.etNewpwd.text.toString()
            val newPwdChk = binding.etNewpwdchk.text.toString()

            if (newPwd != newPwdChk) {
                Toast.makeText(this@ChangePwdActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@ChangePwdActivity, "비밀번호 변경 중", Toast.LENGTH_SHORT).show()
                //Firebase Auth 진행
                fbAuth!!.currentUser!!.updatePassword(newPwd).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_LONG).show()
                        userRef!!.child(fbAuth!!.currentUser!!.uid).child("password").setValue(newPwd)
                        finish()
                    } else {
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}