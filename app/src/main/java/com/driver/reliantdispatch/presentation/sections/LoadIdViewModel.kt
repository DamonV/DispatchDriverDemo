package com.driver.reliantdispatch.presentation.sections

import androidx.databinding.ObservableField
import com.driver.reliantdispatch.domain.AppLogic
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined


class LoadIdViewModel: SectionViewModel() {

    override var mItem = CustomObservableField<EbolJoined?>()
    val mLoadId = ObservableField<String>()
    var mInitialEbol: Ebol? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.ebol?.run{
                mInitialEbol = this
                loadId?.let { mLoadId.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as EbolJoined?)?.ebol?.run{
                loadId = mLoadId.get()
            }
            return value
        }
    }

    override fun onSave(): Boolean{
        mRequiredField =
            if (mLoadId.get().isNullOrEmpty()) AppLogic.requiredEbolFields["loadId"]
            else null
        return super.onSave()
    }

    override fun isFormHasChanged(): Boolean{
        mInitialEbol?.run {
            return loadId != mLoadId.get()
        }
        return false
    }

}