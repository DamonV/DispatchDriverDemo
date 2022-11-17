package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class LoginDTO (
    @SerializedName("driver_email")
    val email: String,
    val password: String
)