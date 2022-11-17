package com.driver.reliantdispatch.data.dto

data class ErrorResponseDTO(
    val message: String?,
    val errors: Map<String, Array<String>>

)
/*"message": "The given data was invalid.",
"errors": {
    "send_invoice": [
    "02"
    ],
    "customer.inavailable": [
    "02"
    ],
    "vehicles.0.inspect.pickup_mileage_inop": [
    "02"
    ],
    "vehicles.0.inspect.condition.is_dark": [
    "02"
    ],
    "vehicles.0.inspect.condition.is_rain": [
    "02"
    ],
    "vehicles.0.inspect.condition.is_dirty": [
    "02"
    ],
    "vehicles.0.inspect.condition.is_uninspect": [
    "02"
    ],
}*/
/*
{
    "message": "The given data was invalid.",
    "errors": {
    "pickup.contact_name": [
    "01"
    ],
    "pickup.phone1": [
    "01"
    ],
    "delivery.company_name": [
    "01"
    ],
    "delivery.contact_name": [
    "01"
    ],
    "delivery.address1": [
    "01"
    ],
    "delivery.zip_code": [
    "01"
    ],
    "delivery.city": [
    "01"
    ],
    "delivery.state": [
    "01"
    ],
    "delivery.phone1": [
    "01"
    ],
    "shipper.phone1": [
    "01"
    ],
    "vehicles.0.running": [
    "01"
    ],
}
}*/