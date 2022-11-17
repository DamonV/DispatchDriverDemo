package com.driver.reliantdispatch.domain

import android.graphics.Bitmap
import com.driver.reliantdispatch.data.toBase64String
import com.driver.reliantdispatch.data.toBitmap

class CustomerSignatureUseCase {

    /*private val files: FilesGateway = App.component.getFilesGateway()

    fun getSignature(filename: String) = files.getSignature(filename)

    suspend fun saveSignature(bitmap: Bitmap) = files.saveSignature(bitmap)

    fun deleteSignature(filename: String) = files.deleteSignature(filename)*/

    fun getSignature(signatureStr: String) = signatureStr.toBitmap()

    fun saveSignature(bitmap: Bitmap) = bitmap.toBase64String()
}