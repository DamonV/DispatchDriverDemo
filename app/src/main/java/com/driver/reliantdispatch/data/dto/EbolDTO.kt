package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class EbolDTO(
    var id: Long? = null,

    var status: Int? = 0,

    @SerializedName("load_id")
    var loadId: String? = null,

    @SerializedName("trailer_type")
    var trailerType: String? = null,           //Int

    @SerializedName("pickup_date1")
    var shipDateStr: String? = null,

    @SerializedName("pickup_date")
    var pickUpDateStr: String? = null,

    @SerializedName("pickup_estimate")
    var pickUpDateType: String? = null,           //Int

    @SerializedName("delivery_date")
    var deliveryDateStr: String? = null,

    @SerializedName("delivery_estimate")
    var deliveryDateType: String? = null,           //Int

    @SerializedName("delayed_pickup_date")
    var delayedPickUpDateStr: String? = null,

    @SerializedName("delayed_pickup_estimate")
    var delayedPickUpDateType: String? = null,           //Int

    @SerializedName("delayed_delivery_date")
    var delayedDeliveryDateStr: String? = null,

    @SerializedName("delayed_delivery_estimate")
    var delayedDeliveryDateType: String? = null,           //Int

    @SerializedName("delayed_reason")
    var delayReasons: Array<Int>? = null,                  //array of indexes delay reasons

    @SerializedName("send_invoice")
    var sendInvoice: Boolean? = false,

    @SerializedName("additional_info")
    var additionalInfo: String? = null,

    @SerializedName("shipper")
    var shipperCompany: CompanyDTO? = null,

    @SerializedName("pickup")
    var pickUpCompany: CompanyDTO? = null,

    @SerializedName("delivery")
    var deliveryCompany: CompanyDTO? = null,

    @SerializedName("pickup_customer")
    var customer: CustomerDTO? = null,

    @SerializedName("payment")
    var payment: PaymentDTO? = null,

    @SerializedName("vehicles")
    var vehiclesList: MutableList<VehicleDTO>? = null
)