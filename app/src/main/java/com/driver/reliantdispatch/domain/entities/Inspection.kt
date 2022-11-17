package com.driver.reliantdispatch.domain.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "inspections")
data class Inspection(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    var odometer: String? = null,
    var odometerInop: Boolean? = null,
    var additionalInspectNotes: String? = null,

    var isDark: Boolean? = null,
    var isSnow: Boolean? = null,
    var isRain: Boolean? = null,
    var isDirty: Boolean? = null,
    var isUninspect: Boolean? = null,

    @Embedded
    var additionalInspection: AdditionalInspection? = null
)