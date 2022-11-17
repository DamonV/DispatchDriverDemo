package com.driver.reliantdispatch.presentation

import androidx.databinding.ObservableField
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.AuthUseCase
import com.driver.reliantdispatch.domain.ValidationRules
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.launch

class ForgotViewModel: ScopedViewModel(){
    val mEmail = ObservableField<String>()

    private val mValidationRules = ValidationRules()
    private val mAuthUseCase = AuthUseCase()

    fun onReset(){
        if (!mValidationRules.isValidEmail(mEmail.get()))
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.getString(R.string.error_validation_email)
            )
        else {
            uiScope.launch {
                val response = mAuthUseCase.forgot(mEmail.get()!!)
                if (response.success)
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_info,
                        context.resources.getString(R.string.text_forgot_instructions)
                    )
                else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
                else viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        response.errorMsg
                    )
            }
        }
    }

    fun onClose (){
        viewModelEvent.value = PresentationEvent(EventType.NAVIGATE_UP)
    }

    fun onTryConnectionAgain(){
        onReset()
    }
}