package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName


data class VehicleShortDTO(
    var id: Long? = null,

    @SerializedName("inspect")
    var inspection: InspectionDTO? = null

)
