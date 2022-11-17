package com.driver.reliantdispatch.domain.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey

@Entity(tableName = "damage_marks", foreignKeys = [
    ForeignKey(entity = InspectImage::class, parentColumns = arrayOf("id"), childColumns = arrayOf("inspectImageId"), onDelete = CASCADE)
])
data class DamageMark(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var inspectImageId: Long = 0,     //foreign-key to "InspectImage" table (M:1 relationship)

    var damageKind: Int,          // index in "damage kind constant array"
    var x: Int,                   //    x - coordinate in 0..width pixels of image
    var y: Int                    //    y - coordinate in 0..height pixels of image
)