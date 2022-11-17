package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class CustomerDTO(

    @SerializedName("customer_name")
    var customerName: String? = null,

    @SerializedName("emails")
    var customerEmails: List<String>? = null,

    @SerializedName("customer_signature")
    var customerSignature: String? = null,

    @SerializedName("available")
    var customerAvailability: Boolean? = null,

    @SerializedName("inavailable")
    var customerInavailability: Boolean? = null
)
