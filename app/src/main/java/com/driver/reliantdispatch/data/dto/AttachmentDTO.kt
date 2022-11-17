package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class AttachmentDTO(
    @SerializedName("storage_path")
    val attachmentUrl: String? = null,

    @SerializedName("file_name")
    val attachmentName: String? = null
)
