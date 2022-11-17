package com.driver.reliantdispatch.presentation.sections

import androidx.databinding.ObservableField
import com.driver.reliantdispatch.domain.entities.DamageMark
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined


class DamageMarkingViewModel : SectionViewModel(){
    var mInspectionType: Int = -1

    var mInspectImage: InspectImageJoined? = null

    val mDamagesList = ObservableField<MutableList<DamageMark>>()
    var mInitial: MutableList<DamageMark>? = null

    val mCurImages = ObservableField<String>()
    val mDateTaken = ObservableField<String>()


    fun onStart(){
        setCurImage()
        mDamagesList.get()?.let { list ->
        }
    }

    private fun setCurImage(){
        mInspectImage?.let{ inspectImageJoined ->
            inspectImageJoined.inspectImage?.let{
                mCurImages.set(it.fileUrl)
                mDateTaken.set(it.dateTaken)
            }
            val list = inspectImageJoined.damagesList
            mDamagesList.set(MutableList(list.size) { list[it].copy()})
            mInitial = MutableList(list.size) { list[it].copy()}
        }
    }

    override fun isFormHasChanged(): Boolean{
        return mInitial != mDamagesList.get()
    }

}