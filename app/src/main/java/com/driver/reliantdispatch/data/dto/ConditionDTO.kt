package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class ConditionDTO(
    @SerializedName("is_dark")
    var isDark: Boolean? = null,

    @SerializedName("is_snow")
    var isSnow: Boolean? = null,

    @SerializedName("is_rain")
    var isRain: Boolean? = null,

    @SerializedName("is_dirty")
    var isDirty: Boolean? = null,

    @SerializedName("is_uninspect")
    var isUninspect: Boolean? = null
)