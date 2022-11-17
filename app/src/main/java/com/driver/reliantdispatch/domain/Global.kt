package com.driver.reliantdispatch.domain

import android.content.IntentSender
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.PreferencesGateway
import com.driver.reliantdispatch.domain.dto.CountersDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.Profile
import com.driver.reliantdispatch.presentation.REQ_CHECK_SETTINGS
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.util.*
import javax.inject.Inject
import android.content.ContextWrapper
import android.app.Activity
import android.content.Context


const val LOG_TAG = "RLNT"
const val LOCATION_INTERVAL: Long = 1
const val LOCATION_FASTEST_INTERVAL: Long = 1
const val LOCATION_TIMEOUT: Long = 60000

const val ARG_FIELD = "argfield"
const val ARG_READ_ONLY = "argreadonly"
const val ARG_1 = "arg1"
const val ARG_2 = "arg2"

const val PICKUP_INSPECTION = 1
const val DELIVERY_INSPECTION = 2

const val URL_API = "http://reliant-dispatch.ustage.sharp-dev.net/app_api/"//"https://reliantdispatch.com/"
const val URL_BASE = "http://reliant-dispatch.ustage.sharp-dev.net/"

const val RUNNING_YES = 1
const val RUNNING_NO = 2        //index in string array "yes_no"

const val FRAGMENT_PICKUP = 0
const val FRAGMENT_DELIVERY = 1


class Global{
    @Inject
    lateinit var preferences: PreferencesGateway

    //var isChecked = false

    var isCleanLogin = false

    var listNeedRefreshByStatus: EbolStatus? = null

    val mLocationPermissionGranted = MutableLiveData<Boolean>()

    val mLocationSettingsRequest = MutableLiveData<Boolean?>()

    val mProfile = MutableLiveData<Profile?>()

    //val mCounters = MutableLiveData<CountersDTO?>()

    init{
        App.component.inject(this)
        mLocationPermissionGranted.value = false
    }

    var mUser: String? = null
        set(value) {
            preferences.setUser(value)
            field = value
        }
        get() {
            if (field == null){
                field = preferences.getUser()
            }
            return field
        }

    var mToken: String? = null
        set(value) {
            preferences.setAccessToken(value)
            field = value
            //Log.d(LOG_TAG, "set mToken $field")
        }
        get() {
            if (field == null){
                field = preferences.getAccessToken()
            }
            //Log.d(LOG_TAG, "get mToken $field")
            return field
        }

    var mExpiresAt: Long? = null        //current date + expiresIn in ms
        set(value) {
            preferences.setExpiresIn(value)
            field = value
            //Log.d(LOG_TAG, "set mExpiresAt $field")
        }
        get() {
            if (field == null){
                field = preferences.getExpiresIn()
            }
            //Log.d(LOG_TAG, "get mExpiresAt $field")
            return field
        }

    var mRememberMe: Boolean? = null
        set(value) {
            preferences.setRememberMe(value)
            field = value
            //Log.d(LOG_TAG, "set RememberMe $field")
        }
        get() {
            if (field == null){
                field = preferences.getRememberMe()
            }
            //Log.d(LOG_TAG, "get RememberMe $field")
            return field
        }

    var mInterval: Int? = null
        set(value) {
            preferences.setInterval(value)
            field = value
        }
        get() {
            if (field == null){
                field = preferences.getInterval()
            }
            return field
        }


    fun isTokenExpired() = mExpiresAt!=null && mExpiresAt!=0L && mExpiresAt!! < Date().time

    fun setExpiresAt(expires_in: Long?) = expires_in?.let { mExpiresAt = Date().time + it*1000}

    companion object{
        @JvmStatic
        fun checkLocationSettings(activity: Activity){
            val locationRequest = LocationRequest.create()?.apply {
                interval = LOCATION_INTERVAL
                fastestInterval = LOCATION_FASTEST_INTERVAL
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            locationRequest?.let {
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(it)
                val client = LocationServices.getSettingsClient(activity)

                val task = client.checkLocationSettings(builder.build())
                task.addOnFailureListener { exception ->
                    if (exception is ResolvableApiException){
                        try {
                            exception.startResolutionForResult(activity,
                                REQ_CHECK_SETTINGS
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            Log.d(LOG_TAG, "settings exception", sendEx)
                        }
                    }
                }
            }
        }
    }
}

fun Context.getActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}