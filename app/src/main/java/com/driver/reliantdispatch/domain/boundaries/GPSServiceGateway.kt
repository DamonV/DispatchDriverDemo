package com.driver.reliantdispatch.domain.boundaries

import android.content.Context

interface GPSServiceGateway {
    fun start(context: Context, interval: Long)

    fun setNextStart(context: Context, interval: Long)

    fun cancelNextStart(context: Context, interval: Long)
}