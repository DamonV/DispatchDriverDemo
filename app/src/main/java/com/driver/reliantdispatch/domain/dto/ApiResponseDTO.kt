package com.driver.reliantdispatch.domain.dto

data class ApiResponseDTO (
    val body: Any? = null,
    val success: Boolean = false,
    val errorMsg: String? = null,
    val noInternet: Boolean = false,
    val notAuthorized: Boolean = false,
    val partialSuccess: Boolean = false
)