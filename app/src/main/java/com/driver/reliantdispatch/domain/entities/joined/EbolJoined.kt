package com.driver.reliantdispatch.domain.entities.joined

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.driver.reliantdispatch.domain.entities.Company
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.Vehicle

data class EbolJoined(
    @Embedded
    val ebol: Ebol? = Ebol(),

    @Relation(parentColumn = "shipperCompanyId", entityColumn = "id")
    var shipperCompany: MutableList<Company> = mutableListOf(),

    @Relation(parentColumn = "pickUpCompanyId", entityColumn = "id")
    var pickUpCompany: MutableList<Company> = mutableListOf(),

    @Relation(parentColumn = "deliveryCompanyId", entityColumn = "id")
    var deliveryCompany: MutableList<Company> = mutableListOf(),

    @Relation(parentColumn = "id", entityColumn = "ebolId", entity = Vehicle::class)
    var vehiclesList: MutableList<VehicleJoined> = mutableListOf()

){
    @Ignore
    var posted: Boolean = false


    val shipperComp: Company?
        get() = if (shipperCompany.isNotEmpty()) shipperCompany[0] else null

    val pickUpComp: Company?
        get() = if (pickUpCompany.isNotEmpty()) pickUpCompany[0] else null

    val deliveryComp: Company?
        get() = if (deliveryCompany.isNotEmpty()) deliveryCompany[0] else null

}