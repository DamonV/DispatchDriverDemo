package com.driver.reliantdispatch.data.dto

data class ResponseInspectionDTO(
    val inspect_id: Long?,
    val items: List<ResponseInspectItemDTO>?
){
    data class ResponseInspectItemDTO(
        val id: Long?,
        val index: Int?
    )
}
