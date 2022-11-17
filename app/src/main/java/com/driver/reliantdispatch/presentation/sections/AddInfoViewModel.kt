package com.driver.reliantdispatch.presentation.sections

import androidx.databinding.ObservableField
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined

class AddInfoViewModel: SectionViewModel() {

    override var mItem = CustomObservableField<EbolJoined?>()
    val mAdditionalInfo = ObservableField<String>()
    var mInitialEbol: Ebol? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.ebol?.run{
                mInitialEbol = this
                additionalInfo?.let { mAdditionalInfo.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as EbolJoined?)?.ebol?.run{
                additionalInfo = mAdditionalInfo.get()
            }
            return value
        }
    }

    override fun isFormHasChanged(): Boolean{
        mInitialEbol?.run {
            return additionalInfo != mAdditionalInfo.get()
        }
        return false
    }
}