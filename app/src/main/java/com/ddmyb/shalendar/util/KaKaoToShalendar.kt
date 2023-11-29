package com.ddmyb.shalendar.util

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class KaKaoToShalendar:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras

        initDeepLink()
    }
    private fun initDeepLink() {
        if (Intent.ACTION_VIEW == intent.action) {
            val uri = intent.data
            if (uri != null) {
                // ⭐️여기서 androidExecutionParams 값들을 받아와 어떠한 상세페이지를 띄울지 결정할 수 있음
                val productIdx = uri.getQueryParameter("productIdx")
                val payMode = uri.getQueryParameter("pay_mode")

                Log.d("oz","${productIdx} testinitDeepLink")
                Log.d("oz", "${payMode} ")
                val bundle = Bundle()
                if (productIdx != null) {
                    bundle.putInt("product_idx", productIdx.toInt())
                    /*togetherFragment.arguments = bundle
                    benefitFragment.arguments = bundle*/

                    if (payMode == "benefit") {
                        //benefitFragment로 이동
                    } else {
                        //benefitFragment로 이동
                    }
                }
            }
        }
    }
}