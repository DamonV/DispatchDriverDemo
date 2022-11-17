package com.driver.reliantdispatch.domain

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.VinGateway
import com.driver.reliantdispatch.domain.boundaries.getSafe

class VehicleInfoUseCase {

    private val vinApi: VinGateway = App.component.getVinGateway()

    private val res = App.component.getResourcesGateway()

    fun isWideLoadNeeded(pos: Int): Boolean = res.vehicleTypesApiList?.get(pos)?.wideLoad == "Yes"
        /*when(pos){
            13, 17, 22, 23, 24 -> true
            else -> false
        }*/

    suspend fun decodeVin(value: String) = vinApi.decodeVin(value)

}