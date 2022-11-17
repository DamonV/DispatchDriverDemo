package com.driver.reliantdispatch.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.driver.reliantdispatch.domain.entities.Ebol

//@Entity(tableName = "drafted_ebols")
class DraftedEbolDBEntity(
    /*loadId: String?,
    trailerType: String?,
    paymentAmount: String?,
    paymentMethod: String?,
    paymentMade: String?,*/
    @PrimaryKey val id: Int
)/*: Ebol(
    loadId,
    trailerType,
    paymentAmount,
    paymentMethod,
    paymentMade
)*/