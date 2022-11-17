package com.driver.reliantdispatch.domain.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "attachments", foreignKeys = [
    ForeignKey(entity = Inspection::class, parentColumns = arrayOf("id"), childColumns = arrayOf("inspectionId"), onDelete = ForeignKey.CASCADE)
])
data class Attachment(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var inspectionId: Long = 0,

    val fileType: String? = null,              //PDF, DOC, PPT, XLS, TXT
    val attachmentUrl: String? = null,         //local file URL or web-URL
    val attachmentName: String? = null

){
    @Ignore
    var posted: Boolean? = false


    companion object{
        val fileTypes = arrayOf("PDF", "DOC", "PPT", "XLS", "TXT")

        fun getFileTypeFromPath(path: String?): String {
            var type = fileTypes.last()
            if (path != null) {
                val ext = path.substring(path.lastIndexOf(".") + 1).toUpperCase()
                for (item in fileTypes) {
                    if (ext.indexOf(item) == 0) {
                        type = item
                        break
                    }
                }
            }
            return type
        }
    }
}