package com.driver.reliantdispatch.presentation.binding

import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.kyanogen.signatureview.SignatureView

@BindingMethods(
    BindingMethod(type = SignatureView::class, attribute = "bitmap", method = "setBitmap")
)
object SignatureViewBindingAdapter {

    /*@JvmStatic
    @BindingAdapter("damagesList")
    fun setDamagesList(view: MarkingImageView, value: MutableList<DamageMark>?) {
        value?.let{
            view.damagesList = it
        }
    }*/
}