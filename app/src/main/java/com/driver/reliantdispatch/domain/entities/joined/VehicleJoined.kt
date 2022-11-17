package com.driver.reliantdispatch.domain.entities.joined

import androidx.room.Embedded
import androidx.room.Relation
import com.driver.reliantdispatch.domain.entities.Inspection
import com.driver.reliantdispatch.domain.entities.Vehicle

data class VehicleJoined(
    @Embedded
    var vehicle: Vehicle?,

    @Relation(parentColumn = "pickupInspectionId", entityColumn = "id", entity = Inspection::class)
    var pickupInspection: MutableList<InspectionJoined> = mutableListOf(),

    @Relation(parentColumn = "deliveryInspectionId", entityColumn = "id", entity = Inspection::class)
    var deliveryInspection: MutableList<InspectionJoined> = mutableListOf()

) {
    val pickupInspect: InspectionJoined?
        get() = if (pickupInspection.isNotEmpty()) pickupInspection[0] else null

    val deliveryInspect: InspectionJoined?
        get() = if (deliveryInspection.isNotEmpty()) deliveryInspection[0] else null
}