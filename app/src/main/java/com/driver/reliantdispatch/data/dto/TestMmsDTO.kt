package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class TestMmsDTO(
    @SerializedName("driver_cell_number")
    val cellPhone: String?,

    @SerializedName("mms_carrier_id")
    val carrierId: Int?
)