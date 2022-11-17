package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class PasswordDTO(
    val password: String,

    @SerializedName("new_password")
    val newPassword: String
)