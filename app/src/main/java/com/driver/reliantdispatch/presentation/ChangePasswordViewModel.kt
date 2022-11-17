package com.driver.reliantdispatch.presentation

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.data.dto.ErrorResponseDTO
import com.driver.reliantdispatch.domain.MyProfileUseCase
import com.driver.reliantdispatch.domain.ValidationRules
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ChangePasswordViewModel : ScopedViewModel() {

    val mOldPassword = ObservableField<String>()
    val mNewPassword = ObservableField<String>()
    val mConfirmPassword = ObservableField<String>()
    val mChangeBtnEnabled = ObservableBoolean(true)

    private val mValidationRules = ValidationRules()

    var mMainViewModel: MainViewModel? = null


    fun onClickChange(){
        if (!mValidationRules.isValidPassword(mOldPassword.get()))
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.getString(R.string.error_validation_old_password)
            )
        else if (!mValidationRules.isValidNewPassword(mNewPassword.get(), mConfirmPassword.get()))
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.getString(R.string.error_validation_new_password)
            )
        else uiScope.launch {
            mMainViewModel?.mProgressVisibility?.set(true)
            mChangeBtnEnabled.set(false)
            val response = MyProfileUseCase().changePassword(mOldPassword.get() ?: "", mNewPassword.get() ?: "")
            async {
                delay(600)
                mChangeBtnEnabled.set(true)
                mMainViewModel?.mProgressVisibility?.set(false)
            }
            if (response.success) {
                viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_info,
                    context.getString(R.string.text_change_password_success)
                )
            } else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
            else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
            else if (response.body is ErrorResponseDTO) {
                if (response.body.errors.toList().any { it.first == "new_password" })
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        context.resources.getString(R.string.error_new_password_requirements)
                    )
                if (response.body.errors.toList().any { it.first == "password" })
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        context.resources.getString(R.string.error_old_password)
                    )
                else viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    context.resources.getString(R.string.error_fields_required) + "\n" + response.body.errors.toList()[0].first
                )
            }
            else viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                response.errorMsg
            )
        }
    }
}