package com.driver.reliantdispatch.domain.entities

enum class EbolStatus (
    val text: String
){
    DRAFTED("Drafted"),
    ASSIGNED("Assigned"),
    PICKED_UP("Picked Up"),
    DELIVERED("Delivered"),
    PAID("Paid"),
    CANCELED("Canceled"),
    ARCHIVED("Archived")
}