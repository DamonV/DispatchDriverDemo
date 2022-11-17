package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PaymentDTO(
    @SerializedName("amount")
    var paymentAmount: BigDecimal? = null,

    @SerializedName("payment_method")
    var paymentMethod: String? = null,             //Int

    @SerializedName("payment_when")
    var paymentType: String? = null,             //Int

    @SerializedName("pickup_amount_received")
    var paymentAmountReceived: BigDecimal? = null,

    @SerializedName("delivery_amount_received")
    var paymentDeliveryAmountReceived: BigDecimal? = null,

    @SerializedName("delivery_received_comment")
    var comment: String? = null

)