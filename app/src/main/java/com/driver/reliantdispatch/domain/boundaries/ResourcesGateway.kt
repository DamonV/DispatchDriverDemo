package com.driver.reliantdispatch.domain.boundaries

import com.driver.reliantdispatch.domain.dto.VehicleTypeDTO

fun Array<String>.getSafe(index: Int?): String =
    if (index != null) if (index in 0 until this.size) this[index]
                        else ""
    else ""

fun Array<String>.getIndexSafe(str: String?): Int =
    if (str != null) {
        val index = this.indexOf(str)
        if (index in 0 until this.size) index
        else 0
    } else 0

fun List<VehicleTypeDTO>?.getSafe(index: Int?): String =
    if (this != null && index != null)
        if (index in 0 until this.size) this[index].typeName ?: ""
        else ""
    else ""

fun List<VehicleTypeDTO>?.getIndexSafe(str: String?): Int =
    if (this != null && str != null) {
        val index = this.indexOfFirst { it.typeName == str }
        if (index in 0 until this.size) index
        else 0
    } else 0


interface ResourcesGateway {

    val colorArray: Array<String>

    val trailerTypeArray: Array<String>

    val vehicleDriveTypeArray: Array<String>

    val paymentTypeShortArray: Array<String>

    val paymentMethodArray: Array<String>

    val datesTypesArray: Array<String>
    
    val stateCodesArray: Array<String>

    val stateNamesArray: Array<String>

    val vehicleTypesArray: Array<String>

    val driveTypesVinauditArray: Array<String>

    val damageKindsArray: Array<String>

    val damageKindsShortArray: Array<String>

    val additionalInspectionShortArray: Array<String>

    val yesNoArray: Array<String>

    var vehicleTypesApiList: List<VehicleTypeDTO>?
}