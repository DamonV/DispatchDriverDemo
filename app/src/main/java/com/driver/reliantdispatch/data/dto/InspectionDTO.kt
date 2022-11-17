package com.driver.reliantdispatch.data.dto

import com.driver.reliantdispatch.domain.entities.AdditionalInspection
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal


data class InspectionDTO(
    @SerializedName("pickup_mileage")
    var odometerPickup: BigDecimal? = null,           //String

    @SerializedName("pickup_mileage_inop")
    var odometerInopPickup: Boolean? = null,              //Boolean

    @SerializedName("pickup_addition")
    var additionalInspectNotesPickup: String? = null,

    @SerializedName("delivery_mileage")
    var odometerDelivery: BigDecimal? = null,           //String

    @SerializedName("delivery_mileage_ino")
    var odometerInopDelivery: Boolean? = null,              //Boolean

    @SerializedName("delivery_addition")
    var additionalInspectNotesDelivery: String? = null,

    @SerializedName("additional_inspection")
    var additionalInspection: AdditionalInspection? = null,

    var condition: ConditionDTO? = null,

    @SerializedName("items")
    var imagesList: MutableList<InspectImageDTO> = mutableListOf(),

    @SerializedName("attach_files")
    var attachmentList: MutableList<AttachmentDTO> = mutableListOf()
)
