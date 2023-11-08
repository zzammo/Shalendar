package com.ddmyb.shalendar.view.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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

class CustomNewCalendarDialog : DialogFragment() {
    private lateinit var binding: DialogNewCalendarBinding
    private var selectedImageUri: Uri? = null
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

        /*binding.dncEditProfileIv.setOnClickListener {
            val editDialog = EditProfileDIalog()
            editDialog.setClickListener(object : EditProfileDialogInterface {
                override fun onCameraClicked() {
                    openCamera()
                    editDialog.dismiss()
                }
                override fun onAlbumClicked() {
                    openAlbum()
                    editDialog.dismiss()
                }
                override fun onDelPhotoClicked() {
                    binding.dncProfileIv.setImageResource(R.drawable.ic_person)
                    editDialog.dismiss()
                }
            })
            editDialog.show(requireFragmentManager(), "EditProfileDialog")
        }*/

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
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openAlbum() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, ALBUM_REQUEST_CODE)
    }

    /*@Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    if (imageBitmap != null) {
                        binding.dncProfileIv.setImageBitmap(imageBitmap)
                        selectedImageUri = getImageUri(requireContext(), imageBitmap)
                    }
                }
                ALBUM_REQUEST_CODE -> {
                    selectedImageUri = data?.data
                    if (selectedImageUri != null) {
                        Glide.with(this).load(selectedImageUri).into(binding.dncProfileIv)
                    }
                }
            }
        }
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }*/

    /*private fun deletePhotoFromStorage(photoUrl: String) {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl)
        storageRef.delete()
            .addOnSuccessListener {
                // File deleted successfully
                // You might also want to delete the reference to this photo from your Firebase Database if you're using it to store references to photos.
            }
            .addOnFailureListener {
                // Handle any errors that occur during the deletion
            }
    }*/
}
