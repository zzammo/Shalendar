package com.ddmyb.shalendar.view.dialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ddmyb.shalendar.databinding.DialogNewCalendarBinding
import com.ddmyb.shalendar.domain.FBTest
import com.ddmyb.shalendar.domain.groups.repository.GroupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ParticipateTeamMateDialog : DialogFragment() {
    private lateinit var binding: DialogNewCalendarBinding
    private var dialogListener: DialogListener? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogNewCalendarBinding.inflate(inflater, container, false)
        binding.dncNameEt.hint = "참여 코드를 입력해주세요"
        binding.dncOkBtn.setOnClickListener {
            val participateCode = binding.dncNameEt.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                GroupRepository().inviteGroup(participateCode)
                dismiss()
            }
        }
        binding.dncCancelBtn.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    fun setDialogListener(listener: DialogListener){
        this.dialogListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dialogListener?.onDialogClosed("Dialog is closed")
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
