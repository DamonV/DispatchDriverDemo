package com.driver.reliantdispatch.domain

import android.app.Activity
import android.util.Log
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.boundaries.LocationGateway
import com.driver.reliantdispatch.domain.boundaries.ResourcesGateway
import com.driver.reliantdispatch.domain.boundaries.getSafe
import com.driver.reliantdispatch.domain.entities.InspectImage
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class InspectionUseCase: ScopedUseCase() {
    @Inject
    lateinit var api: ApiGateway

    @Inject
    lateinit var locationService: LocationGateway

    @Inject
    lateinit var resources: ResourcesGateway

    init {
        App.component.inject(this)
    }

    suspend fun initSilhouette(type: Int?): InspectImage {
        val inspectImage = InspectImage()
        /*ioScope.async {
            delay(2000)
            locationService.getCurrentLocationAddress()?.let {
                inspectImage.locationAddress =
                    "${it.address}, ${it.city}, ${resources.stateCodesArray.getSafe(it.state)}, ${it.zip}"
                Log.d(LOG_TAG, "address ${inspectImage.locationAddress}")
            }
            Log.d(LOG_TAG, "retrieving address ends")
        }*/

        val vehicleType = type?.let{ resources.vehicleTypesApiList?.get(type) }
        vehicleType?.let{
            inspectImage.dateTaken = SimpleDateFormat("MM-dd-yyyy 'at' HH:mm z", Locale.US).format(Date())
            inspectImage.fileUrl = URL_BASE + it.silhouetteUrl
        }
        /*addressJob.await()?.let{
        }*/
        return inspectImage
    }

    suspend fun initInspectImage(): InspectImage {
        val inspectImage = InspectImage(
            dateTaken = SimpleDateFormat("MM-dd-yyyy 'at' hh:mm z", Locale.US).format(Date()))
        /*inspectImage.locationAddress = getInspectAddress(activity)
        Log.d(LOG_TAG, "address ${inspectImage.locationAddress}")*/
        return inspectImage
    }

    suspend fun getInspectAddress(activity: Activity?) = locationService.getCurrentLocationAddress(activity)?.let {
        "${it.address}, ${it.city}, ${resources.stateCodesArray.getSafe(it.state)}, ${it.zip}"
    }

}