package com.driver.reliantdispatch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.driver.reliantdispatch.domain.Global
import com.driver.reliantdispatch.domain.boundaries.GPSServiceGateway
import javax.inject.Inject

class BootBroadcastReceiver: BroadcastReceiver() {
    @Inject
    lateinit var global: Global

    @Inject
    lateinit var gpsService: GPSServiceGateway

    init {
        App.component.inject(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        gpsService.start(context, (global.mInterval ?: 0) * 3600 * 1000L)
    }

}