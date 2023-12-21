package com.ddmyb.shalendar.view.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.databinding.ActivityRegisterBinding
import com.ddmyb.shalendar.domain.users.UserAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SignUpActivity : AppCompatActivity() {
    private var fbAuth: FirebaseAuth? = null //파이어베이스 인증 처리
    private var userRef: DatabaseReference? = null //실시간 데이터베이스

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fbAuth = FirebaseAuth.getInstance() // 파이어베이스 데이터베이스 연동
        userRef = FirebaseDatabase.getInstance().getReference("UserAccount")

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            //회원가입 처리 시작
            val strEmail = binding.etEmail.getText().toString()
            val strPwd = binding.etPwd.getText().toString()
            val strNick = binding.etNickname.getText().toString()
            val strPwdChk = binding.etPwdchk.getText().toString()
            if(strPwd!=strPwdChk){
                Toast.makeText(this@SignUpActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }
            else if(strNick==null){
                Toast.makeText(this@SignUpActivity, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this@SignUpActivity, "회원가입 진행 중", Toast.LENGTH_SHORT).show()
                //Firebase Auth 진행
                fbAuth!!.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(this@SignUpActivity)
                { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = fbAuth!!.currentUser
                        val account = UserAccount().apply {
                            userId = firebaseUser!!.uid
                            emailId = firebaseUser.email
                            password = strPwd
                            nickName = strNick
                        }
                        userRef!!.child(firebaseUser!!.uid).setValue(account)
                        Toast.makeText(this@SignUpActivity, "회원가입에 성공하셨습니다", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, NickNameActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignUpActivity, "중복된 아이디이거나 비밀번호 조건을 충족하지 않습니다", Toast.LENGTH_LONG).show()
                        Toast.makeText(this, task.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }
}