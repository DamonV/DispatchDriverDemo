package com.driver.reliantdispatch.presentation.sections

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.dto.RequiredField
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class SectionViewModel: ScopedViewModel() {
    var mField: Int = -1        //focused field
    var mRequiredField: RequiredField? = null     //found required field

    open val mItem = ObservableField<EbolJoined?>()

    open val mFocused = Array(12) {ObservableBoolean()}

    open fun setFocus(field: Int){
        mField = field
        if (field in 0 until mFocused.size) mFocused[field].set(true)
    }

    fun resetFocus(){
        if (mField > -1) {
            uiScope.launch {
                delay(300)
                if (mField in 0 until mFocused.size) mFocused[mField].set(false)
                mField = -1
            }
        }
    }

    open fun onSave(): Boolean{
        mRequiredField?.let {
            val fieldName = if (it.fieldNameResId !=null ) context.getString(it.fieldNameResId)
                            else ""
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.resources.getString(R.string.error_check_load_id, fieldName)
            )
            setFocus(it.fieldNumber)
            return false
        }
        return true
    }

    open fun isFormHasChanged(): Boolean{
        return false
    }
}