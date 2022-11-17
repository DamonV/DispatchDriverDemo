package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class CityApiDTO(
    val city: String?,
    val state: String?,
    @SerializedName("zip_code") val postal: String?,
    @SerializedName("metro_info") val metroInfo: String?
    /*val latitude: Double?,
    val longitude: Double?,
    val timezone: String?,
    val value: String?*/
)