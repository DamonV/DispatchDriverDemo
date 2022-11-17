package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class VehicleDTO(
    var id: Long? = null,

    var year: Int? = null,      //String

    var make: String? = null,

    var model: String? = null,

    @SerializedName("vin_code")
    var vin: String? = null,

    @SerializedName("trim")
    var trim_: String? = null,

    var length: BigDecimal? = null,

    var width: BigDecimal? = null,

    var height: BigDecimal? = null,

    @SerializedName("weight")
    var curbWeight: BigDecimal? = null,

    @SerializedName("license")
    var licensePlate: String? = null,

    @SerializedName("additional_info")
    var addInfo: String? = null,

    @SerializedName("vin_na")
    var vinNA: Int? = null,

    @SerializedName("wide_load")
    var wideLoad: Boolean? = null,              //Boolean !

    @SerializedName("forklift_required")
    var forklift: Boolean? = null,              //Boolean !

    var type: String? = null,       //Int

    @SerializedName("drive_type")
    var driveType: String? = null,             //Int

    var running: String? = null,             //Int

    var state: String? = null,             //Int

    @SerializedName("color")
    var exteriorColor: String? = null,         //Int

    @SerializedName("pickup_inspect")
    var pickupInspection: InspectionDTO? = null,

    @SerializedName("delivery_inspect")
    var deliveryInspection: InspectionDTO? = null,

    var quantity: Int? = 1
)
