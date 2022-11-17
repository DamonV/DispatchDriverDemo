package com.driver.reliantdispatch.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

const val PNG_PREFIX = "data:image/png;base64,"


fun String?.toBitmap(): Bitmap? = this?.let{
    val str = if (it.indexOf(PNG_PREFIX) == 0) {
        it.substring(PNG_PREFIX.length)
    } else it
    val byteArr = Base64.decode(str, Base64.DEFAULT)
    val options = BitmapFactory.Options()
    //options.inPreferredConfig = Bitmap.Config.ARGB_8888
    options.inMutable = true
    BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size, options)
}

fun Bitmap?.toBase64String() = this?.let{
    val output = ByteArrayOutputStream()
    it.compress(Bitmap.CompressFormat.PNG, 100, output)
    PNG_PREFIX + Base64.encodeToString(output.toByteArray(), Base64.DEFAULT)
}