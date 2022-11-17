package com.driver.reliantdispatch.data

import android.content.Context
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import com.driver.reliantdispatch.App
import javax.inject.Inject


class ConnectivityService {
    @Inject
    lateinit var context: Context
    init {
        App.component.inject(this)
    }

    private val mConnectivityManager: ConnectivityManager? by lazy {
        getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
    }

    fun getNetworkAvailability(): Boolean {
        mConnectivityManager?.activeNetworkInfo?.let {
            if (it.isConnected) return true
        }
        return false
    }
}