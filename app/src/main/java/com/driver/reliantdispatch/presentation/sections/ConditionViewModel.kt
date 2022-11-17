package com.driver.reliantdispatch.presentation.sections

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.InspectionUseCase
import com.driver.reliantdispatch.domain.entities.DamageMark
import com.driver.reliantdispatch.domain.entities.InspectImage
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_RETAKE_IMAGE_CAPTURE = 2


class ConditionViewModel : SectionViewModel(){
    var mInspectionType: Int = -1

    var mImageIndex: Int = 0

    val mImagesList = ObservableField<MutableList<InspectImageJoined>>()

    val mCurImages = ObservableField<String>()
    val mDateTaken = ObservableField<String>()
    val mDamagesList = ObservableField<MutableList<DamageMark>>()

    val mShowDamages = ObservableBoolean(true)

    val mInspectionUseCase = InspectionUseCase()
    var mInitial: MutableList<InspectImageJoined>? = null


    fun onStart(){
        setCurImage()
        mImagesList.get()?.let { list ->
            mInitial = mutableListOf()
            mInitial?.addAll(list)
        }
    }

    fun onClick(v: View){
    }

    fun onSelectImage(pos: Int){
        mImageIndex = pos
        setCurImage()
    }

    private fun setCurImage(){
        mImagesList.get()?.let{ list ->
            if (mImageIndex in 0 until list.size) with(list[mImageIndex]){
                mDamagesList.set(damagesList)
                inspectImage?.let{
                    mCurImages.set(it.fileUrl)
                    mDateTaken.set(it.dateTaken)
                }
            }
        }
    }

    fun onPhotoTaken(fileUri: String?, requestCode: Int, activity: Activity?){
        Log.d(LOG_TAG, "photo taken $fileUri")
        ioScope.launch {
            var address = ""
            var inspectImage: InspectImage? = null
            async {
                mInspectionUseCase.getInspectAddress(activity)?.let {
                    address = it
                    if (inspectImage != null) inspectImage?.locationAddress = it
                    Log.d(LOG_TAG, "address $address")
                }
                Log.d(LOG_TAG, "retrieving address ends")
            }
            inspectImage = mInspectionUseCase.initInspectImage()
            inspectImage.fileUrl = fileUri
            inspectImage.locationAddress = address

            val inspectImageJoined = InspectImageJoined(inspectImage, mutableListOf())
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    mImagesList.set(mImagesList.get()?.let {
                        val newList = mutableListOf<InspectImageJoined>()
                        newList.addAll(it)
                        newList.add(inspectImageJoined)
                        newList
                    })
                    mImagesList.get()?.let{onSelectImage(it.size-1)}
                }
                REQUEST_RETAKE_IMAGE_CAPTURE -> {
                    mImagesList.set(mImagesList.get()?.let {
                        val newList = mutableListOf<InspectImageJoined>()
                        newList.addAll(it)
                        Log.d(LOG_TAG, "in list $newList")
                        Log.d(LOG_TAG, "replace index $mImageIndex")
                        if (mImageIndex in 0 until newList.size) newList[mImageIndex] = inspectImageJoined
                        else newList.add(inspectImageJoined)
                        newList
                    })
                    setCurImage()
                }
            }
        }
    }

    fun onStop(){
        mInspectionUseCase.onStop()
    }

    override fun isFormHasChanged(): Boolean{
        return mInitial != mImagesList.get()
    }

}