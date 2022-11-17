package com.driver.reliantdispatch.data

import android.content.Context
import android.util.Log
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.VinGateway
import com.driver.reliantdispatch.domain.boundaries.getIndexSafe
import com.driver.reliantdispatch.domain.entities.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject


class VinGatewayImpl: VinGateway {
    @Inject
    lateinit var context: Context

    @Inject
    lateinit var retrofitService: VinRetrofitService

    init {
        App.component.inject(this)
    }

    override suspend fun decodeVin(value: String): Vehicle? =  withContext(Dispatchers.IO) {
        try {
            val response = retrofitService.getSpecifications.queryAsync(value, VIN_API_KEY, "json").await()
            if (response.isSuccessful && response.body()!= null) {
                val vinResponse = response.body()!!
                if (vinResponse.success && vinResponse.attributes!=null){
                    val res = App.component.getResourcesGateway()
                    with (vinResponse.attributes) {
                        Log.d(LOG_TAG, "$vehicleType")
                        return@withContext Vehicle(0, 0,
                            year,
                            make,
                            model,
                            vin,
                            trim_,
                            overallLength,
                            overallWidth,
                            overallHeight,
                            curbWeight,
                            type = res.vehicleTypesApiList.getIndexSafe(convertVehicleType(vehicleType)),
                            driveType = res.driveTypesVinauditArray.getIndexSafe(drivenWheels)
                        )
                    }
                } else {
                    Log.d(LOG_TAG, "vin api error ${vinResponse.error}")
                }
            } else {
                Log.d(LOG_TAG, "server error ${response.code()}")
            }
        } catch (e: HttpException) {
            Log.d(LOG_TAG, "http exception ${e.message()}", e)
        } catch (e: Throwable) {
            Log.d(LOG_TAG, e.message)
        }
        return@withContext null
    }

    private fun convertVehicleType(type: String?) = when (type){
        "Sport Utility Vehicle" -> "SUV"
        else -> type
    }
}