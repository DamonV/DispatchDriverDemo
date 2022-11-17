package com.driver.reliantdispatch.domain.dto

data class ZipEntryDTO(
    val city: String?,
    val stateIndex: Int,
    val state: String?,
    val postal: String?
){
    val value: String
        get() = "$postal $city $state"

    override fun toString() = value
}