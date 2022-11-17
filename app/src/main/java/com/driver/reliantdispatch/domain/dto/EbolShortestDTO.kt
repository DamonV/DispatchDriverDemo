package com.driver.reliantdispatch.domain.dto

import com.driver.reliantdispatch.domain.entities.EbolStatus


data class EbolShortestDTO(
    var id: Long? = null,

    var status: Int? = 0
){
    val statusE: EbolStatus?
        get() = status?.let{
            try {
                EbolStatus.values()[it]
            } catch(e: IndexOutOfBoundsException) {null}
        }
}