package com.ddmyb.shalendar.view.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.domain.users.UserAccount
import com.ddmyb.shalendar.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증 처리
    private var mDatabaseRef: DatabaseReference? = null //실시간 데이터베이스

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mFirebaseAuth = FirebaseAuth.getInstance() // 파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UserAccount")

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            //회원가입 처리 시작
            val strNickname = binding.etNickname.getText().toString()
            val strEmail = binding.etEmail.getText().toString()
            val strPwd = binding.etPwd.getText().toString()
            Toast.makeText(this@SignUpActivity, "회원가입 진행 중", Toast.LENGTH_SHORT).show()
            //Firebase Auth 진행
            mFirebaseAuth!!.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(this@SignUpActivity)
            { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mFirebaseAuth!!.currentUser
                    val account = UserAccount().apply {
                        userId = firebaseUser!!.uid
                        emailId = firebaseUser.email
                        nickName = strNickname
                        password = strPwd
                    }
                    mDatabaseRef!!.child(firebaseUser!!.uid).setValue(account)
                    Toast.makeText(this@SignUpActivity, "회원가입에 성공하셨습니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@SignUpActivity, "회원가입에 실패하셨습니다", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}