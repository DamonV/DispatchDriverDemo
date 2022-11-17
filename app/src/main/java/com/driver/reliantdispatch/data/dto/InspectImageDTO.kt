package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class InspectImageDTO(

    @SerializedName("file_created_date")
    var dateTaken: String? = null,          //formatted date with time-zone and time

    @SerializedName("geo_tag")
    var locationAddress: String? = null,       //address of the place where image was taken

    @SerializedName("file_path")
    var fileUrl: String? = null,             //local file URL or web-URL

    @SerializedName("damage_list")
    var damagesList: MutableList<DamageMarkDTO>? = mutableListOf(),

    var index: Int?
)

/*
    "type": "string",
"kind": 0,

 */
/*
id": 0,
"is_drawing": 0,
"file_path": "string",
"file_created_at": "string",
"geo_tag": "string",
"type": "string",
"kind": 0,
"file_created_date": "string",
"file_created_time": "string",*/