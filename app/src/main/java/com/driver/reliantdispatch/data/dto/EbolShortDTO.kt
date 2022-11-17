package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class EbolShortDTO(
    var id: Long? = null,

    var status: Int? = 0,

    @SerializedName("send_invoice")
    var sendInvoice: Boolean? = null,

    @SerializedName("additional_info")
    var additionalInfo: String? = null,

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

    @SerializedName("customer")
    var customer: CustomerDTO? = null,

    @SerializedName("payment")
    var payment: PaymentDTO? = null,

    @SerializedName("vehicles")
    var vehiclesList: MutableList<VehicleShortDTO>? = null
)

/*{
  "send_invoice": 0,
  "id": 0,
  "status": 0,
  "delayed_pickup_date": "2019-08-09",
  "delayed_pickup_estimate": "string",
  "delayed_delivery_date": "2019-08-09",
  "delayed_delivery_estimate": "string",
  "delayed_reason": [
    0
  ],
  "payment": {
    "pickup_amount_received": 0,
    "delivery_amount_received": 0,
    "delivery_received_comment": "string"
  },
  "customer": {
    "customer_name": "string",
    "customer_signature": "string",
    "inavailable": 0,
    "available": 0,
    "emails": [
      "string"
    ]
  },
  "vehicles": [
    {
      "id": 0,
      "inspect": {
        "pickup_mileage": 0,
        "pickup_mileage_inop": 0,
        "pickup_addition": "string",
        "delivery_mileage": 0,
        "delivery_mileage_ino": 0,
        "delivery_addition": "string",
        "additional_inspection": {
          "windshield": 0,
          "missingParts": 0,
          "wheels": 0,
          "wheelsAmount": "string",
          "vehicleStarts": 0,
          "engineVibration": 0,
          "engineNoise": 0,
          "engineSmoke": 0,
          "clutchIssues": 0,
          "brakesIssues": 0,
          "leaking": 0,
          "electricalIssues": 0,
          "checkEngineLight": 0,
          "brakeABSLight": 0,
          "airbagLight": 0,
          "tractionControlLight": 0,
          "smartKey": 0,
          "otherKeys": 0,
          "manuals": 0
        },
        "items": [
          {
            "geo_tag": "string",
            "type": "string",
            "index": 0,
            "file_created_date": "string",
            "damage_list": [
              {
                "type": 0,
                "x": 0,
                "y": 0
              }
            ]
          }
        ],
        "condition": {
          "is_dark": 0,
          "is_snow": 0,
          "is_rain": 0,
          "is_dirty": 0,
          "is_uninspect": 0
        }
      }
    }
  ]
}
*/