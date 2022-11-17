package com.driver.reliantdispatch.domain

import android.content.Context
import android.util.Log
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.GPSServiceGateway
import javax.inject.Inject


class GPSServiceUseCase {
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var global: Global

    @Inject
    lateinit var gpsService: GPSServiceGateway

    private var mInterval: Long = 0L

    private var mPrevLocationSettings: Boolean? = null

    init {
        App.component.inject(this)

        global.mProfile.observeForever{ profile ->

            Log.d(LOG_TAG, "GPSServiceUseCase update interval ${profile?.locationSharing} / ${profile?.longitude} - ${profile?.latitude}")

            val interval = if (profile?.locationSharing != null) {
                global.mInterval = profile.locationSharing
                profile.locationSharing!! * 3600 * 1000L
            }
            else mInterval
            handleIntervalChange(interval)
        }

        global.mLocationSettingsRequest.observeForever { value ->
            if (mPrevLocationSettings != true && value == true) {
                mInterval = 0
                startReliantService()
            }
            mPrevLocationSettings = value
        }
    }

    fun startReliantService(){
        Log.d(LOG_TAG, "GPSServiceUseCase start ${global.mProfile.value?.locationSharing}")
        val interval = (global.mProfile.value?.locationSharing ?: 0) * 3600 * 1000L
        handleIntervalChange(interval)
    }

    private fun handleIntervalChange(interval: Long){
        if (mInterval != 0L && interval == 0L)
            gpsService.cancelNextStart(context, mInterval)
        else if (mInterval != interval) {
            gpsService.start(context, interval)
        }

        mInterval = interval
    }

}