package com.driver.reliantdispatch.presentation

import androidx.databinding.ObservableField
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.MyProfileUseCase
import com.driver.reliantdispatch.domain.entities.Profile
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.launch


class MyProfileViewModel : ScopedViewModel() {
    private val global = App.component.getGlobal()

    val mProfile = ObservableField<Profile?>()

    init {
        mProfile.set(global.mProfile.value)
    }

    fun onRefresh(){
        uiScope.launch {
            mProgressVisibility.set(true)
            val response = MyProfileUseCase().getProfile()
            mProgressVisibility.set(false)
            if (response.success){
                val profile = response.body as Profile
                mProfile.set(profile)
                global.mProfile.postValue(profile)
            }
            else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
            else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
            else viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                response.errorMsg
            )
        }
    }

    fun onClickEdit(){
        viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_driver_signature)
    }
}