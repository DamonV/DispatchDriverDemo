package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class ProfileDTO(
    var id: Long? = null,

    @SerializedName("driver_name")
    var driverName: String? = null,

    @SerializedName("driver_cell_number")
    var driverCell: String? = null,

    @SerializedName("driver_email")
    var driverEmail: String? = null,

    @SerializedName("trailer_car_space")
    var trailerCarSpace: String? = null,

    @SerializedName("driver_signature")
    var driverSignature: String? = null,

    @SerializedName("mms_carrier_id")
    var mmsCarrierId: Int? = null,

    var latitude: Float? = null,

    var longitude: Float? = null,

    @SerializedName("location_sharing")
    var locationSharing: Int? = null
)
/*"id": 11,
"driver_name": "Driver Test",
"driver_cell_number": "(555) 555-5555",
"driver_email": "dr@mail.ru",
"trailer_car_space": "asfsdf",
"driver_signature": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAfQAAADICAYAAAAeGRPoAAAgAElEQVR4Xu2df8hfVR3Hz5CcysIGzpYtbSDh/NE2nLUo0PmPfxRMQ
"mms_carrier_id": 17,
"latitude": 0,
"longitude": 0,
"location_sharing": 2*/