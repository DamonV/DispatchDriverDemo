package com.driver.reliantdispatch.domain.boundaries

import android.graphics.Bitmap
import android.net.Uri
import okhttp3.ResponseBody

interface FilesGateway {
    suspend fun saveSignature(bitmap: Bitmap): String?

    fun getSignature(filename: String): Bitmap?

    fun deleteSignature(filename: String)

    fun getFileAsString(filename: String): String?

    fun getFileAsBytes(filename: String): ByteArray

    fun writeBodyToDisk(body: ResponseBody, filename: String, extension: String): Uri?
}