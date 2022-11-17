package com.driver.reliantdispatch.domain.dto

data class LoginResponseDTO (
    val token: String?,
    val expires_in: Long?
)