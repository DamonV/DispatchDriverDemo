package com.driver.reliantdispatch.domain.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "inspect_images", foreignKeys = [
    ForeignKey(entity = Inspection::class, parentColumns = arrayOf("id"), childColumns = arrayOf("inspectionId"), onDelete = ForeignKey.CASCADE)
])
data class InspectImage(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var inspectionId: Long = 0,

    var dateTaken: String? = null,          //formatted date with time-zone and time
    var locationAddress: String? = null,       //address of the place where image was taken
    var fileUrl: String? = null             //local file URL or web-URL
)