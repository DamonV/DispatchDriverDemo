package com.driver.reliantdispatch.domain.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.driver.reliantdispatch.App

@Entity(tableName = "vehicles", foreignKeys = [
    ForeignKey(entity = Ebol::class, parentColumns = arrayOf("id"), childColumns = arrayOf("ebolId"), onDelete = ForeignKey.CASCADE),
    ForeignKey(entity = Inspection::class, parentColumns = arrayOf("id"), childColumns = arrayOf("pickupInspectionId"), onDelete = ForeignKey.SET_DEFAULT),
    ForeignKey(entity = Inspection::class, parentColumns = arrayOf("id"), childColumns = arrayOf("deliveryInspectionId"), onDelete = ForeignKey.SET_DEFAULT)
])
data class Vehicle(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var ebolId: Long = 0,                //foreign-key to "Ebol" table (M:1 relationship)

    var year: String? = null,
    var make: String? = null,
    var model: String? = null,
    var vin: String? = null,
    var trim_: String? = null,
    var length: String? = null,
    var width: String? = null,
    var height: String? = null,
    var curbWeight: String? = null,
    var licensePlate: String? = null,
    var addInfo: String? = null,

    var vinNA: Boolean? = null,
    var wideLoad: Boolean? = null,
    var forklift: Boolean? = null,

    var type: Int? = null,
    var driveType: Int? = null,             //index in "driveType constant array"
    var running: Int? = null,
    var state: Int? = null,
    var exteriorColor: Int? = null,         //index in "exterior color constant array"

    var pickupInspectionId: Long? = null,    // foreign-key in "Inspection" table (1:1 relationship)
    var deliveryInspectionId: Long? = null   // foreign-key in "Inspection" table (1:1 relationship)
){
    companion object {
        @JvmStatic
        var vehicleTypesList: List<String>? = null
            get() {
                if (field == null)
                    field = App.component.getResourcesGateway().vehicleTypesApiList?.map { it.typeName ?: "" }
                return field
            }
    }
}