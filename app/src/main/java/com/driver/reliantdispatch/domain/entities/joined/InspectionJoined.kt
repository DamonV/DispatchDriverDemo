package com.driver.reliantdispatch.domain.entities.joined

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.driver.reliantdispatch.domain.entities.Attachment
import com.driver.reliantdispatch.domain.entities.InspectImage
import com.driver.reliantdispatch.domain.entities.Inspection

data class InspectionJoined(
    @Embedded
    var inspection: Inspection? = Inspection(),

    @Relation(parentColumn = "id", entityColumn = "inspectionId", entity = InspectImage::class)
    var imagesList: MutableList<InspectImageJoined> = mutableListOf(),

    @Relation(parentColumn = "id", entityColumn = "inspectionId")
    var attachmentList: MutableList<Attachment> = mutableListOf(),

    @Ignore
    var apiId: Long? = null
)