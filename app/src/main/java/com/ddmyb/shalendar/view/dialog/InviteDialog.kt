package com.ddmyb.shalendar.view.dialog

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.ddmyb.shalendar.databinding.DialogInviteCodeBinding
import com.ddmyb.shalendar.util.KakaoInvite
import com.kakao.sdk.common.KakaoSdk

class InviteDialog(private val code:String) : DialogFragment(){
    val binding: DialogInviteCodeBinding by lazy {
        DialogInviteCodeBinding.inflate(layoutInflater)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val kakao = KakaoInvite(requireContext())
        KakaoSdk.init(requireContext(), "c40c35ac9a1bb7aecc977be650bdbdfc")
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.textViewLink.text = code
        binding.dicCopyBtn.setOnClickListener {
            val clipboard: ClipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", code) // Replace "YourTextHere" with the text you want to copy
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "클립보드에 복사되었습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
        }


        binding.dicSendBtn.setOnClickListener{
            kakao.sendKakaoLink("팀 Shalendar로 초대합니다", code, "")
            dismiss()
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog?.setCancelable(true) // Close the dialog when clicking outside.
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
    }
}
