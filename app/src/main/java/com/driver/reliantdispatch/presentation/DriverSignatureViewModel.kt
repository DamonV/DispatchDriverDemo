package com.driver.reliantdispatch.presentation

import android.graphics.Bitmap
import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.data.dto.ErrorResponseDTO
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.MMSCarrierUseCase
import com.driver.reliantdispatch.domain.MyProfileUseCase
import com.driver.reliantdispatch.domain.dto.CarrierDTO
import com.driver.reliantdispatch.domain.entities.Profile
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DriverSignatureViewModel : ScopedViewModel() {
    private val global = App.component.getGlobal()

    val mMyProfileUseCase = MyProfileUseCase()
    val mMMSCarrierUseCase = MMSCarrierUseCase()
    val mProfile = CustomObservableField<Profile?>()
    var mMainViewModel: MainViewModel? = null
    val mCarrierList = ObservableField<MutableList<String>>()
    var mCarriers = listOf<CarrierDTO>()

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            if (value!=null) {
                with(value as Profile) {
                    driverName?.let { mDriverName.set(it) }
                    driverCell?.let { mDriverCell.set(it) }
                    mmsCarrierId?.let { mCarrierId = it }
                    trailerCarSpace?.let { mTrailerCarSpace.set(it) }
                    driverEmail?.let { mDriverEmail.set(it) }
                    driverSignature?.let {
                        mSignatureBitmap.set(it)
                        mSignatureExist.set(true)
                    }
                }
            }
        }

        override fun get(): T? {
            val value = super.get()
            if (value!=null) {
                with(value as Profile) {
                    driverName = mDriverName.get()
                    driverCell = mDriverCell.get()
                    mCarrierIndex.get().let{
                        if (it < mCarriers.size) mmsCarrierId = mCarriers[it].id
                    }
                    trailerCarSpace = mTrailerCarSpace.get()
                    driverEmail = mDriverEmail.get()
                }
            }
            return value
        }
    }

    val mDriverName = ObservableField<String>()
    val mDriverCell = ObservableField<String>()
    var mCarrierId: Int = 0
    val mCarrierIndex = ObservableInt()
    val mTrailerCarSpace = ObservableField<String>()
    val mDriverEmail = ObservableField<String>()
    val mSignatureBitmap = ObservableField<Bitmap>()
    var mSignatureExist = ObservableBoolean(false)

    val mSaveBtnEnabled = ObservableBoolean(true)
    val mTestBtnEnabled = ObservableBoolean(true)

    init {
        mProfile.set(global.mProfile.value?.copy())

        uiScope.launch {
            val response = mMMSCarrierUseCase.getCarrierList()
            if (response.success){
                mCarriers = response.body as List<CarrierDTO>
                mCarrierList.set(mCarriers.map { it.title ?: "" }.toMutableList())

                mCarriers.indexOfFirst { it.id == mCarrierId }.let{
                    mCarrierIndex.set(if (it == -1) 0 else it)
                }
            }
            else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
            else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
            /*else {
                Log.d(LOG_TAG, "error getting mms carrier list, substitute with resources")
                val strings = context.resources.getTextArray(R.array.mms_carrier_names)
                mCarrierList.set(strings.toMutableList())
            }*/
        }
    }

    fun onClearSign() {
        mSignatureExist.set(false)
    }

    fun onSave(bitmap: Bitmap, isBitmapEmpty: Boolean){
        if (!isBitmapEmpty){
            mProfile.get()?.let { profile ->
                profile.driverSignature = bitmap
                uiScope.launch {
                    mMainViewModel?.mProgressVisibility?.set(true)
                    mSaveBtnEnabled.set(false)
                    val response = mMyProfileUseCase.putProfile(profile)
                    async {
                        delay(600)
                        mSaveBtnEnabled.set(true)
                        mMainViewModel?.mProgressVisibility?.set(false)
                    }
                    if (response.success) {
                        global.mProfile.postValue(profile)
                    } else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
                    else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                    else if (response.body is ErrorResponseDTO) {
                        viewModelEvent.value = PresentationEvent(
                            EventType.SHOW_DIALOG,
                            -1,
                            R.string.dialog_title_error,
                            context.resources.getString(R.string.error_profile_required) + "\n" + response.body.errors.toList()[0].first
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
    }

    fun onTestClick(){
        mCarrierIndex.get().let{
            if (it < mCarriers.size && mDriverCell.get() != null) {
                uiScope.launch {
                    mMainViewModel?.mProgressVisibility?.set(true)
                    mTestBtnEnabled.set(false)
                    val response = mMMSCarrierUseCase.testSMS(mCarriers[it].id ?: 0, mDriverCell.get()!!)
                    async {
                        delay(600)
                        mTestBtnEnabled.set(true)
                        mMainViewModel?.mProgressVisibility?.set(false)
                    }
                    if (response.success) viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_info,
                        context.resources.getString(R.string.text_sms_success)
                    )
                    else if (response.noInternet) viewModelEvent.value =
                        PresentationEvent(EventType.NO_INTERNET)
                    else if (response.notAuthorized) viewModelEvent.value =
                        PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                    else viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_error,
                        response.errorMsg
                    )
                }
            }
        }
    }

}