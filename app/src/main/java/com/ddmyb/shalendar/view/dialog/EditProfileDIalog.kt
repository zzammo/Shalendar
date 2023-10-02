package com.ddmyb.shalendar.view.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.ddmyb.shalendar.databinding.DialogEditProfileBinding

class EditProfileDIalog : DialogFragment() {//다이얼로그 또 띄울때 context안받고 DialogFragment상속
    val binding: DialogEditProfileBinding by lazy {
        DialogEditProfileBinding.inflate(layoutInflater)
    }

    private lateinit var editDialogInterface: EditProfileDialogInterface

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.setCancelable(true)

        binding.depCameraTv.setOnClickListener{
            editDialogInterface.onCameraClicked()
        }
        binding.depAlbumTv.setOnClickListener{
            editDialogInterface.onAlbumClicked()
        }
        binding.depDeleteTv.setOnClickListener{
            editDialogInterface.onDelPhotoClicked()
        }
        return dialog
    }
    fun setClickListener(editDialogInterface: EditProfileDialogInterface){
        this.editDialogInterface = editDialogInterface
    }
}