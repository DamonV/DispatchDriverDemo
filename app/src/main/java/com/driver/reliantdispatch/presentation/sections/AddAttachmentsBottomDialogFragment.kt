package com.driver.reliantdispatch.presentation.sections

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.DialogAddAttachmentsBinding
import com.driver.reliantdispatch.presentation.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst
import java.util.ArrayList


class AddAttachmentsBottomDialogFragment: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogAddAttachmentsBinding.inflate(layoutInflater, container, false)
        with (binding) {
            lifecycleOwner = this@AddAttachmentsBottomDialogFragment
            fragment = this@AddAttachmentsBottomDialogFragment
        }
        return binding.root
    }

    fun onClickPhoto(v: View){

        FilePickerBuilder.instance
            .setSelectedFiles(ArrayList())
            .setActivityTheme(R.style.LibAppTheme)
            .enableCameraSupport(true)
            .setCameraPlaceholder(R.drawable.ic_photo_camera)
            .pickPhoto(this)
    }

    fun onClickFile(v: View){
        FilePickerBuilder.instance
            .setSelectedFiles(ArrayList())
            .setActivityTheme(R.style.LibAppTheme)
            .pickFile(this)
    }

    fun onClickCancel(v: View){
        dialog?.dismiss()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        dialog?.dismiss()
        activity?.let {
            val mainViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
            if (resultCode == Activity.RESULT_OK && data != null) {
                when (requestCode) {
                    FilePickerConst.REQUEST_CODE_PHOTO -> {
                        mainViewModel.mPhotosPathArr = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA)
                    }
                    FilePickerConst.REQUEST_CODE_DOC -> {
                        mainViewModel.mFilesPathArr = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS)
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): AddAttachmentsBottomDialogFragment {
            return AddAttachmentsBottomDialogFragment()
        }
    }
}