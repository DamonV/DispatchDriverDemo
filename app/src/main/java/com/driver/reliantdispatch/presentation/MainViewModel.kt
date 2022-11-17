package com.driver.reliantdispatch.presentation

import androidx.databinding.ObservableField
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.AuthUseCase
import com.driver.reliantdispatch.domain.entities.AdditionalInspection
import com.driver.reliantdispatch.domain.entities.Profile
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel


class MainViewModel: ScopedViewModel(){
    var isNoInternet: Boolean = false

    var mImagesList: MutableList<InspectImageJoined>? = null
    var mInspectImage: InspectImageJoined? = null
    var mAdditionalInspection: AdditionalInspection? = null
    var mPhotosPathArr: ArrayList<String>? = null
    var mFilesPathArr: ArrayList<String>? = null

    var mNavBottomDestination: Int? = null
    var mNavDestination: Int? = null

    val mProfile = ObservableField<Profile?>()

    init {
        App.component.getGlobal().mProfile.observeForever { profile ->
            mProfile.set(profile)
        }
    }

    suspend fun logout()= AuthUseCase().logout()

}