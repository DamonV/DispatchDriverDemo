package com.driver.reliantdispatch.domain

import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.dto.EbolFieldsAvailability
import com.driver.reliantdispatch.domain.dto.EbolFieldsVisibility
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.presentation.dto.RequiredField

object AppLogic {

    //required fields for drafted save - fragment, field number, fragment type
    val requiredEbolFields = mapOf(              // navId: Int, field: Int, fragmentType: Int
        "loadId" to RequiredField(R.id.nav_load_id, 0, -1, R.string.label_loadid),
        "shipDate" to RequiredField(R.id.nav_shipping_dates, 0, -1, R.string.label_shipping_date1),
        "pickUpDate" to RequiredField(R.id.nav_shipping_dates, 1, -1, R.string.label_shipping_date2),
        "deliveryDate" to RequiredField(R.id.nav_shipping_dates, 2, -1, R.string.label_shipping_date3),
        "paymentAmount" to RequiredField(R.id.nav_payment, 0, -1, R.string.label_payment_amount),
        "shipper_city" to RequiredField(R.id.nav_shipper_consignor, 5, -1, R.string.label_city),
        "pickup_contact" to RequiredField(R.id.nav_pickup_delivery, 1, 0, R.string.label_contact_name),
        "pickup_address" to RequiredField(R.id.nav_pickup_delivery, 2, 0, R.string.label_address),
        "pickup_zip" to RequiredField(R.id.nav_pickup_delivery, 4, 0, R.string.label_zip),
        "pickup_city" to RequiredField(R.id.nav_pickup_delivery, 5, 0, R.string.label_city),
        "pickup_state" to RequiredField(R.id.nav_pickup_delivery, 6, 0, R.string.label_state),
        "pickup_phone" to RequiredField(R.id.nav_pickup_delivery, 7, 0, R.string.label_phone),
        "delivery_address" to RequiredField(R.id.nav_pickup_delivery, 2, 1, R.string.label_address),
        "delivery_zip" to RequiredField(R.id.nav_pickup_delivery, 4, 1, R.string.label_zip),
        "delivery_city" to RequiredField(R.id.nav_pickup_delivery, 5, 1, R.string.label_city),
        "delivery_state" to RequiredField(R.id.nav_pickup_delivery, 6, 1, R.string.label_state),
        "delivery_phone" to RequiredField(R.id.nav_pickup_delivery, 7, 1, R.string.label_phone),
        "customerName" to RequiredField(R.id.nav_customer_signature, 0, -1, R.string.label_customer_name1),
        "customerEmail" to RequiredField(R.id.nav_customer_signature, 1, -1, R.string.label_email)
    )

    val requiredVehicleFields = mapOf(              // field number: Int
        "vin" to RequiredField(R.id.nav_vehicle_info, 0, -1, R.string.label_vin1),
        "year" to RequiredField(R.id.nav_vehicle_info, 1, -1, R.string.label_year1),
        "make" to RequiredField(R.id.nav_vehicle_info, 2, -1, R.string.label_make1),
        "model" to RequiredField(R.id.nav_vehicle_info, 3, -1, R.string.label_model1),
        "type" to RequiredField(R.id.nav_vehicle_info, 4, -1, R.string.hint_type),
        "running" to RequiredField(R.id.nav_vehicle_info, 5, -1, R.string.hint_running)
    )

    val requiredVehicleInspectionFields = mapOf(              // navId: Int, field: Int, fragmentType: Int
        "odometer" to RequiredField(R.id.nav_inspection, 0, -1, R.string.label_vehicle_odometer3)
    )

    val requiredAdditionalInspectionFields = mapOf(              // navId: Int, field: Int, fragmentType: Int
        "wheelsAmount" to RequiredField(R.id.nav_additional_inspection, 0, -1, R.string.label_number_wheels)
    )

    @JvmStatic
    fun ebolAvailability(status: EbolStatus?): EbolFieldsAvailability = when(status) {
        EbolStatus.DRAFTED -> EbolFieldsAvailability()
        EbolStatus.ASSIGNED -> EbolFieldsAvailability(
            false,
            false,
            false,
            true,
            true,
            false,
            true,
            false,
            false,
            false,
            false
        )
        EbolStatus.PICKED_UP -> EbolFieldsAvailability(
            false,
            false,
            false,
            false,
            true,
            false,
            true,
            false,
            false,
            false,
            false,
            false
        )
        else -> EbolFieldsAvailability()
    }

    @JvmStatic
    fun ebolVisibility(status: EbolStatus?): EbolFieldsVisibility = when(status) {
        EbolStatus.DRAFTED, EbolStatus.ASSIGNED -> EbolFieldsVisibility()
        else -> EbolFieldsVisibility(
            true
        )
    }
}