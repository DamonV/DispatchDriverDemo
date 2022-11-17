package com.driver.reliantdispatch.presentation.dto

data class RequiredField(
    val fragmentId: Int,
    val fieldNumber: Int,
    val fragmentType: Int,
    val fieldNameResId: Int? = null
)