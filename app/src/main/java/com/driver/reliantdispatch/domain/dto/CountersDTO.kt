package com.driver.reliantdispatch.domain.dto

import com.driver.reliantdispatch.domain.entities.EbolStatus

data class CountersDTO(
    var drafted: Int? = null,
    var assigned: Int?,
    val picked_up: Int?,
    val delivered: Int?,
    val paid: Int?,
    val canceled: Int?,
    val archived: Int?
){
    fun getCounterByStatus(status: EbolStatus) = when(status){
        EbolStatus.ASSIGNED -> assigned
        EbolStatus.PICKED_UP -> picked_up
        EbolStatus.DELIVERED -> delivered
        EbolStatus.PAID -> paid
        EbolStatus.CANCELED -> canceled
        EbolStatus.ARCHIVED -> archived
        EbolStatus.DRAFTED -> drafted
    }
}

