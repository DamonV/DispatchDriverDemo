package com.driver.reliantdispatch.data

import android.app.Activity
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.*
import com.driver.reliantdispatch.domain.boundaries.LocationGateway
import com.driver.reliantdispatch.domain.dto.LocationAddressDTO
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import java.io.IOException
import java.lang.Exception
import java.lang.Runnable
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class LocationGatewayImpl: LocationGateway {

    @Inject lateinit var context: Context

    @Inject lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    @Inject lateinit var mGeocoder: Geocoder

    @Inject lateinit var global: Global

    init {
        App.component.inject(this)
    }

    override suspend fun getCurrentLocationAddress(activity: Activity?): LocationAddressDTO? = withContext(Dispatchers.IO) {
        if (App.component.getGlobal().mLocationPermissionGranted.value!!) {
            val location = getDeviceLocation(activity)
            if (location!=null) {
                var addressList: List<Address> = listOf()
                var i = 0
                while (addressList.isEmpty() && i < 3) { //sometime builtin geocoder generate IOException for the first run
                    addressList = getAddressByLocation(location.latitude, location.longitude)
                    i++
                }
                if (addressList.isNotEmpty()) {
                    with(addressList[0]) {
                        val stateArr = context.resources.getStringArray(R.array.state_names)
                        val stateIndex = if (adminArea != null) stateArr.indexOf(adminArea) else -1
                        return@withContext LocationAddressDTO(
                            "${premises ?: ""} ${subThoroughfare ?: ""} ${thoroughfare ?: ""}".trim(),
                            postalCode ?: "",
                            "${subLocality ?: ""} ${locality ?: ""}".trim(),
                            if (stateIndex==-1) 0 else stateIndex
                        )
                    }
                }
            }
        }
        return@withContext null
    }

    override suspend fun getCurrentLocation(): Location? =
        suspendCoroutine { cont ->
            val numCores = Runtime.getRuntime().availableProcessors()
            val executor = ThreadPoolExecutor(
                numCores * 2, numCores * 2,
                30L, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>()
            )
            finalLocationRequest(cont, executor)

            GlobalScope.launch(Dispatchers.IO) {
                delay(LOCATION_TIMEOUT)
                try {
                    cont.resume(null)
                } catch (e: Exception) {
                }
            }
        }

    private fun getAddressByLocation(lat: Double, lng: Double): List<Address> {
        try {
            return mGeocoder.getFromLocation(
                lat,
                lng,
                1
            )
        } catch (e: IOException) {
            Log.d(LOG_TAG, "geocoder IOException", e)       //geocoding service is not available due to a network error or IO exception
        } catch (e: IllegalArgumentException) {
            Log.d(LOG_TAG, "geocoder illegalArgumentException", e)
        }
        return listOf()
    }

    private suspend fun getDeviceLocation(activity: Activity?): Location? =
        suspendCoroutine { cont ->
            try {
                val numCores = Runtime.getRuntime().availableProcessors()
                val executor = ThreadPoolExecutor(
                    numCores * 2, numCores * 2,
                    30L, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>()
                )

                mFusedLocationProviderClient
                .lastLocation
                .addOnCompleteListener(executor, OnCompleteListener<Location> { task ->
                    if (task.isSuccessful) {
                        // current location of the device.
                        val lastKnownLocation = task.result
                        try {
                            if (lastKnownLocation == null)
                                handleLocationSettings(activity, cont, executor)
                            else {
                                cont.resume(lastKnownLocation)
                            }
                        } catch (e: Exception){
                            Log.d(LOG_TAG, e.message)
                        }
                    } else handleLocationSettings(activity, cont, executor)
                })
            } catch (e: SecurityException) {
                try {
                    cont.resume(null)
                } catch (e: Exception){}
            }

            GlobalScope.launch(Dispatchers.IO) {
                delay(LOCATION_TIMEOUT)
                try {
                    cont.resume(null)
                } catch (e: Exception){}
            }
        }

    private fun handleLocationSettings(activity: Activity?, cont: Continuation<Location?>, executor: ThreadPoolExecutor){
        if (activity != null) {
            GlobalScope.launch(Dispatchers.Main) {
                global.mLocationSettingsRequest.observeForever(object : Observer<Boolean?> {
                    override fun onChanged(granted: Boolean?) {
                        granted?.let {
                            global.mLocationSettingsRequest.removeObserver(this)
                            global.mLocationSettingsRequest.value = null
                            if (!it) {
                                cont.resume(null)
                            } else finalLocationRequest(cont, executor)
                        }
                    }
                })
            }
            Global.checkLocationSettings(activity)
        } else cont.resume(null)
    }

    private fun finalLocationRequest(cont: Continuation<Location?>, executor: ThreadPoolExecutor) {

        var firstMethodRunning = true
        var secondMethodRunning = true

        // First method
        try {
            mFusedLocationProviderClient
                .lastLocation
                .addOnCompleteListener(executor, OnCompleteListener<Location> { task ->
                    firstMethodRunning = false
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result
                        try {
                            if (lastKnownLocation == null) {
                                if (!secondMethodRunning) {
                                    cont.resume(null)
                                }
                            }
                            else {
                                cont.resume(lastKnownLocation)
                            }
                        } catch (e: Exception){}
                    } else {
                        try {
                            if (!secondMethodRunning) {
                                cont.resume(null)
                            }
                        } catch (e: Exception){}
                    }
                })
        } catch (e: SecurityException) {
            if (!secondMethodRunning) {
                try {
                    cont.resume(null)
                } catch (e: Exception){}
            }
        }

        // Second method
        val request = LocationRequest.create()?.apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_FASTEST_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        try {
            if (!mLooperThread.isAlive) {
                mLooperThread.start()
                while (mLooperThread.mLooper == null) Thread.sleep(25)
            }
        } catch (e: IllegalThreadStateException) {
            Log.d(LOG_TAG, "", e)
        }
        try {
            mFusedLocationProviderClient.requestLocationUpdates(request, object : LocationCallback(){

                override fun onLocationResult(locResult: LocationResult) {
                    secondMethodRunning = false
                    try {
                        mFusedLocationProviderClient.removeLocationUpdates(this)
                    } catch (e: SecurityException) {
                        Log.d(LOG_TAG, e.message, e)
                    }
                    try {
                        if (locResult.lastLocation == null) {
                            if (!firstMethodRunning) {
                                cont.resume(null)
                            }
                        }
                        else {
                            cont.resume(locResult.lastLocation)
                        }
                    } catch (e: Exception){}
                }
            }, mLooperThread.mLooper)
        } catch (e: SecurityException) {
            if (!firstMethodRunning) {
                try {
                    cont.resume(null)
                } catch (e: Exception){}
            }
        }
    }

    private val mLooperThread = object : Thread() {
        var mLooper: Looper? = null

        override fun run() {
            Looper.prepare()
            mLooper = Looper.myLooper()
            Looper.loop()
        }
    }
}