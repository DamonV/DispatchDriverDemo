package com.driver.reliantdispatch.presentation.sections

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.domain.ARG_2
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.databinding.FragmentConditionBinding
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.MY_PERMISSIONS_CAMERA_REQ_CODE
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_condition.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ConditionFragment: BaseFragment() {
    lateinit var mBinding: ViewDataBinding
    private var mPhotoUriStr: String? = null
    var mInspectionType: Int = -1
    var isCameraPermissionGranted = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(ConditionViewModel::class.java)

        mBinding = FragmentConditionBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentConditionBinding) {
            lifecycleOwner = this@ConditionFragment
            fragment = this@ConditionFragment
            viewModel = mViewModel as ConditionViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        with (mViewModel as ConditionViewModel) {
            if (mMainViewModel.mInspectImage != null){
                mImagesList.get()?.let { list ->
                    if (mImageIndex in 0 until list.size) {
                        list[mImageIndex] = mMainViewModel.mInspectImage!!
                        mImagesList.set(MutableList(list.size) { list[it].copy() })
                    }
                }
                mMainViewModel.mInspectImage = null
            } else {
                mInspectionType = arguments?.getInt(ARG_1, -1) ?: -1
                this@ConditionFragment.mInspectionType = mInspectionType
                mImageIndex = arguments?.getInt(ARG_2, 0) ?: 0

                if (savedInstanceState != null) {
                    val savedJson = savedInstanceState.getString(ARG_1, "")
                    try {
                        val list = Gson().fromJson(savedJson, Array<InspectImageJoined>::class.java).toMutableList()
                        mImagesList.set(list)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    mPhotoUriStr = savedInstanceState.getString(ARG_2)
                } else mMainViewModel.mImagesList?.let { list ->
                    mImagesList.set(MutableList(list.size) { list[it].copy() })
                }
            }

            checkCameraPermissions()
            onStart()

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.adapter = InspectImagesRecyclerAdapter(this, this@ConditionFragment)
            Log.d(LOG_TAG, "adapter ${recyclerView.adapter}")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (mViewModel as ConditionViewModel).mImagesList.get()?.let{
            outState.putString(ARG_1, Gson().toJson(it))
        }
        outState.putString(ARG_2, mPhotoUriStr)
    }

    fun onSaveClick() {
        mMainViewModel.mImagesList = (mViewModel as ConditionViewModel).mImagesList.get()
        activity?.let {
            Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
        }

        /*val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.save_confirm)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()*/
    }

    fun onCloseClick() {
        mMainViewModel.mImagesList = null
        if ((mViewModel as SectionViewModel).isFormHasChanged()){
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.close_confirm)
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, _ ->
                    dialog.cancel()

                    activity?.let {
                        Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
                    }
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
        } else activity?.let {
            Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
        }
    }

    fun onClickMarkDamage(v: View){
        with (mViewModel as ConditionViewModel) {
            mImagesList.get()?.let{ list ->
                if (mImageIndex in 0 until list.size) mMainViewModel.mInspectImage = list[mImageIndex]
            }
        }
        val args = Bundle()
        args.putInt(ARG_1, mInspectionType)
        Navigation.findNavController(v).navigate(R.id.nav_damage_marking, args)
    }

    fun onClickPhoto(){
        callCamera(REQUEST_IMAGE_CAPTURE)
    }

    fun onClickRetake(){
        callCamera(REQUEST_RETAKE_IMAGE_CAPTURE)
    }

    override fun onBackPressed(): Boolean{
        mMainViewModel.mImagesList = null
        return false
    }

    private fun callCamera(requestCode: Int){
        if (!isCameraPermissionGranted) return
        context?.packageManager?.let {
            if (it.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(it)?.also {
                        val photoFile: File? = try {
                            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
                            val storageDir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            File.createTempFile("reliant_${timeStamp}", ".jpg", storageDir)
                        } catch (ex: IOException) {
                            null
                        }
                        photoFile?.also {
                            activity?.let { activity ->
                                val photoURI = FileProvider.getUriForFile(
                                    activity,
                                    "${activity.packageName}.fileprovider",
                                    it
                                )
                                mPhotoUriStr = photoURI.toString()
                                Log.d(LOG_TAG, "photo output to $mPhotoUriStr")
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                startActivityForResult(takePictureIntent, requestCode)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        if (resultCode == RESULT_OK && mPhotoUriStr!=null) {
            (mViewModel as ConditionViewModel).onPhotoTaken(mPhotoUriStr, requestCode, activity)
        }
    }

    private fun checkCameraPermissions(){
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_CAMERA_REQ_CODE
            )
        } else isCameraPermissionGranted = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_CAMERA_REQ_CODE -> {
                if (grantResults.isNotEmpty()
                    && permissions.any{it == Manifest.permission.CAMERA}
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isCameraPermissionGranted = true
                }
                return
            }
        }
    }

}