package com.driver.reliantdispatch.presentation

import android.content.Intent
import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.AuthUseCase
import com.driver.reliantdispatch.domain.ValidationRules
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.launch

class LoginViewModel: ScopedViewModel(){
    val mEmail = ObservableField<String>()//("dr@mail.ru")
    /*val mEmail = ObservableField<String>("test@test.ru")*/
    val mPassword = ObservableField<String>()//("reliant0000")
    val mRememberMe = ObservableBoolean()

    private val mValidationRules = ValidationRules()
    private val mAuthUseCase = AuthUseCase()

    fun onLogin(){
        if (!mValidationRules.isValidEmail(mEmail.get()))
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.getString(R.string.error_validation_email)
            )
        else if (!mValidationRules.isValidPassword(mPassword.get()))
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.getString(R.string.error_validation_password)
            )
        else {
            uiScope.launch {
                val response = mAuthUseCase.login(mEmail.get()!!, mPassword.get()!!, mRememberMe.get())
                if (response.success){
                    viewModelEvent.value = PresentationEvent(EventType.NAVIGATE_UP)
                }
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

    fun onForgot(){
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            R.id.nav_forgot
        )
    }

    fun onSignUp(){
        val newIntent = Intent(Intent.ACTION_VIEW)
        newIntent.data = Uri.parse("https://reliantdispatch.com/register")
        //newIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        viewModelEvent.value = PresentationEvent(
            EventType.INTENT,
            -1,
            -1,
            null,
            newIntent
        )
    }
}