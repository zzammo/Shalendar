package com.ddmyb.shalendar.view.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.ddmyb.shalendar.R
import com.ddmyb.shalendar.databinding.DialogNewCalendarBinding
import com.ddmyb.shalendar.domain.FBTest
import com.ddmyb.shalendar.domain.groups.repository.GroupRepository
import com.ddmyb.shalendar.domain.schedules.repository.ScheduleDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

/*import com.google.firebase.storage.FirebaseStorage*/

class CustomNewCalendarDialog : DialogFragment(),DialogListener {
    private lateinit var binding: DialogNewCalendarBinding
    private var selectedImageUri: Uri? = null
    private var dialogListener: DialogListener? = null
    companion object {
        const val CAMERA_REQUEST_CODE = 1
        const val ALBUM_REQUEST_CODE = 2
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewCalendarBinding.inflate(inflater, container, false)

        binding.dncOkBtn.setOnClickListener {
            val calendarName = binding.dncNameEt.text.toString()
            if(calendarName==""){
                Toast.makeText(requireContext(),"입력해주세요",Toast.LENGTH_SHORT).show()
            }
            else if(calendarName=="개인 캘린더"){
                Toast.makeText(requireContext(),"이 이름은 사용 불가합니다",Toast.LENGTH_SHORT).show()
            }
            else{
                val code = GroupRepository().createGroup(calendarName)
                val inviteDialog = InviteDialog(code)
                inviteDialog.setDialogListener(this)
                Log.d("oz","customNewCalendar null")
                inviteDialog.show(requireFragmentManager(),"")
                dismiss()
            }
        }

        binding.dncCancelBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
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
    fun setDialogListener(listener: DialogListener?){
        this.dialogListener = listener
    }
    override fun onDialogClosed(message: String?) {
        Log.d("oz","CustomNewCalendarDialog close!!")
        if(dialogListener!=null) {
            dialogListener?.onDialogClosed("Dialog is closed")
        }
    }

}
