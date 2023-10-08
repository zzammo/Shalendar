package com.ddmyb.shalendar

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ddmyb.shalendar.databinding.ActivityLoginBinding
import com.ddmyb.shalendar.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private var mFirebaseAuth: FirebaseAuth? = null //파이어베이스 인증 처리
    private var mDatabaseRef: DatabaseReference? = null //실시간 데이터베이스

    private val binding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        mFirebaseAuth = FirebaseAuth.getInstance() // 파이어베이스 데이터베이스 연동
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("UsersAccount")

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            //회원가입 처리 시작
            val strNickname = binding.etNickname.getText().toString()
            val strEmail = binding.etEmail.getText().toString()
            val strPwd = binding.etPwd.getText().toString()
            Toast.makeText(this@RegisterActivity, "진행중", Toast.LENGTH_SHORT).show()
            //Firebase Auth 진행
            mFirebaseAuth!!.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(this@RegisterActivity)
            { task ->
                if (task.isSuccessful) {
                    val firebaseUser = mFirebaseAuth!!.currentUser
                    val account = UserAccount().apply {
                        idToken = firebaseUser!!.uid
                        emailId = firebaseUser.email
                        nickName = strNickname
                        password = strPwd
                    }
                    Toast.makeText(this@RegisterActivity, "진행중", Toast.LENGTH_SHORT).show()
                    //child는 해당 키 위치로 이동하는 함수입니다.
                    //키가 없는데 "UserAccount"와 firebaseUser.getUid()같이 값을 지정한 경우 자동으로 생성합니다.
                    mDatabaseRef!!.child(firebaseUser!!.uid).setValue(account)
                    //mDatabaseRef!!.child(firebaseUser!!.email.toString().replace(".","_")).setValue(account)
                    Toast.makeText(this@RegisterActivity, "회원가입에 성공하셨습니다", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@RegisterActivity, "회원가입에 실패하셨습니다", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}