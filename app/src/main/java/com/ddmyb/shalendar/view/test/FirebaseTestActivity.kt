package com.ddmyb.shalendar.view.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.ActivityFirebaseTestBinding
import com.ddmyb.shalendar.domain.users.UserRepository

class FirebaseTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirebaseTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@FirebaseTestActivity, R.layout.activity_firebase_test)

        binding.signUpButton.setOnClickListener {
            val id = binding.idEditText.text.toString()
            val pw = binding.pwEditText.text.toString()
//            manageSchedule().signUp(id, pw)
        }

        binding.loginButton.setOnClickListener {
            val id = binding.idEditText.text.toString()
            val pw = binding.pwEditText.text.toString()
            UserRepository().login(id, pw, applicationContext)
        }

        binding.saveScheduleButton.setOnClickListener {
            //manageSchedule().saveSchedule(Alarm())
        }
//        test(object: View.OnClickListener {
//            override fun onClick(p0: View?) {
//                Toast.makeText()
//            }
//        })
//
//        loadAllSchedules("0", "1000", object: TestOn {
//            override fun success() {
//
//            }
//
//            override fun fail() {
//
//            }
//        })
    }

//    fun test(listener: View.OnClickListener) {
//        val view = View()
//        view.setOnClickListener {
//            listener
//        }
//    }
//
//    fun loadAllSchedules(minValue: String,maxValue: String, listener: TestOn) {
//        val mFirebaseAuth = FirebaseAuth.getInstance()
//        val mDatabaseRef = FirebaseDatabase.getInstance()
//
//        val currentUser = mFirebaseAuth.currentUser
//
//        val mRef = mDatabaseRef.getReference("UsersSchedule").child(currentUser!!.uid)
//        val query1: Query = mRef.orderByChild("startLocalDateTime").startAt(minValue)
//        val query2: Query = query1.orderByChild("endLocalDateTime").endAt(maxValue)
//
//        query2.get().addOnSuccessListener {
//            listener.success()
//        }.addOnFailureListener{
//            listener.fail()
//        }
//    }

}