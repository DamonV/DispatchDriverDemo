package com.driver.reliantdispatch.domain.entities

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.driver.reliantdispatch.App

data class Profile(
    //var id: Long?,
    var driverName: String?,
    var driverCell: String?,
    var driverEmail: String?,
    var trailerCarSpace: String?,
    var driverSignature: Bitmap?,
    var mmsCarrierId: Int?,
    var locationSharing: Int?,

    var latitude: Float? = null,
    var longitude: Float? = null

){
    companion object {
        @JvmStatic
        fun getSignatureDrawable(item: Profile?): Drawable? {
            item?.let {
                return BitmapDrawable(App.component.getContext().resources, it.driverSignature)
            }
            return null
        }

        @JvmStatic
        fun getProfileText(item: Profile?): String {
            var result = ""
            item?.let {
                result = "${it.driverName ?: ""}\n${it.driverCell ?: ""}\n" +
                        "${it.trailerCarSpace ?: ""}"
            }
            return result
        }
    }
}
