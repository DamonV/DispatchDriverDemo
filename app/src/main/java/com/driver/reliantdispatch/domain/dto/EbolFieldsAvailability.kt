package com.driver.reliantdispatch.domain.dto

data class EbolFieldsAvailability(
    val loadId: Boolean = true,
    val trailerType: Boolean = true,
    val paymentAmount: Boolean = true,
    val payment: Boolean = true,
    val paymentDelivery: Boolean = true,
    val shippingDates: Boolean = true,
    val shippingDelays: Boolean = true,
    val shipperCompany: Boolean = true,
    val pickUpCompany: Boolean = true,
    val deliveryCompany: Boolean = true,
    val vehicles : Boolean = true,
    val pickupInspection: Boolean = true,
    val deliveryInspection: Boolean = true,
    val customerSignature: Boolean = true,
    val driverSignature: Boolean = true,
    val additionalInfo: Boolean = true,
    val sendInvoice: Boolean = true
)

