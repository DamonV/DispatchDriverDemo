package com.driver.reliantdispatch.domain

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.Vehicle
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.dto.RequiredField

class CreateEbolUseCase {
    val db = App.component.getDbGateway()

    suspend fun getEbols() = db.getEbols()

    fun getEbolsPaged() = db.getEbolsPaged()

    suspend fun saveEbol(ebolJoined: EbolJoined) = db.saveEbol(ebolJoined)

    suspend fun deleteEbol(ebolJoined: EbolJoined) = db.deleteEbol(ebolJoined)

    fun checkEbol(ebolJoined: EbolJoined): RequiredField?{
        if (ebolJoined.ebol?.status == EbolStatus.DRAFTED.ordinal || ebolJoined.ebol?.status == EbolStatus.ASSIGNED.ordinal) {
            if (ebolJoined.ebol.loadId.isNullOrEmpty()) return AppLogic.requiredEbolFields["loadId"]
            else if (ebolJoined.ebol.shipDate.isNullOrEmpty()) return AppLogic.requiredEbolFields["shipDate"]
            else if (ebolJoined.ebol.pickUpDate.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickUpDate"]
            else if (ebolJoined.ebol.deliveryDate.isNullOrEmpty()) return AppLogic.requiredEbolFields["deliveryDate"]
            else if (ebolJoined.ebol.paymentAmount.isNullOrEmpty()) return AppLogic.requiredEbolFields["paymentAmount"]
            //else if (ebolJoined.shipperComp?.city.isNullOrEmpty()) return AppLogic.requiredEbolFields["shipper_city"]         //all remark condition from [08.29.18 change]
            //else if (ebolJoined.pickUpComp?.contact.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickup_contact"]
            else if (ebolJoined.pickUpComp?.address.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickup_address"]
            else if (ebolJoined.pickUpComp?.zip.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickup_zip"]
            else if (ebolJoined.pickUpComp?.city.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickup_city"]
            else if (ebolJoined.pickUpComp?.state.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickup_state"]
            //else if (ebolJoined.pickUpComp?.phone.isNullOrEmpty()) return AppLogic.requiredEbolFields["pickup_phone"]
            /*else if (ebolJoined.deliveryComp?.zip.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_zip"]
            else if (ebolJoined.deliveryComp?.city.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_city"]
            else if (ebolJoined.deliveryComp?.phone.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_phone"]*/
        } else if (ebolJoined.ebol?.status == EbolStatus.PICKED_UP.ordinal) {
            //if (ebolJoined.deliveryComp?.contact.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_contact"]
            //else
            if (ebolJoined.deliveryComp?.address.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_address"]
            else if (ebolJoined.deliveryComp?.zip.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_zip"]
            else if (ebolJoined.deliveryComp?.city.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_city"]
            else if (ebolJoined.deliveryComp?.state.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_state"]
            //else if (ebolJoined.deliveryComp?.phone.isNullOrEmpty()) return AppLogic.requiredEbolFields["delivery_phone"]
        }
        return null         // save has passed validation
    }

    fun checkVehicle(vehicle: Vehicle): RequiredField?{
        if (vehicle.vin.isNullOrEmpty() && vehicle.vinNA != true) return AppLogic.requiredVehicleFields["vin"]
        else if (vehicle.year.isNullOrEmpty()) return AppLogic.requiredVehicleFields["year"]
        else if (vehicle.make.isNullOrEmpty()) return AppLogic.requiredVehicleFields["make"]
        else if (vehicle.model.isNullOrEmpty()) return AppLogic.requiredVehicleFields["model"]
        else if (vehicle.type == null) return AppLogic.requiredVehicleFields["type"]
        else if (vehicle.running.isNullOrEmpty()) return AppLogic.requiredVehicleFields["running"]
        return null
    }
}

fun Int?.isNullOrEmpty() = (this == null || this == 0)