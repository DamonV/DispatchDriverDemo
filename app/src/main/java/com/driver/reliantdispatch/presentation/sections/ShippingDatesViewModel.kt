package com.driver.reliantdispatch.presentation.sections

import android.view.MotionEvent
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.domain.AppLogic
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ShippingDatesViewModel : SectionViewModel(){

    override var mItem = CustomObservableField<EbolJoined?>()
    var mInitialEbol: Ebol? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.ebol?.run{
                mInitialEbol = this
                shipDate?.let { mShipDate = str2Date(it) }
                pickUpDate?.let { mPickUpDate = str2Date(it) }
                deliveryDate?.let { mDeliveryDate = str2Date(it) }
                pickUpDateType?.let { mPickUpDateType.set(it) }
                deliveryDateType?.let { mDeliveryDateType.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as EbolJoined?)?.ebol?.run{
                shipDate = mShipDateStr.get()
                pickUpDate = mPickUpDateStr.get()
                deliveryDate = mDeliveryDateStr.get()
                pickUpDateType = mPickUpDateType.get()
                deliveryDateType = mDeliveryDateType.get()
            }
            return value
        }
    }

    val mShipDateStr = ObservableField<String>()
    var mShipDate = Date()
        set(value) {
            mShipDateStr.set(date2Str(value))
        }

    val mPickUpDateStr = ObservableField<String>()
    var mPickUpDate = Date()
        set(value) {
            mPickUpDateStr.set(date2Str(value))
        }

    val mDeliveryDateStr = ObservableField<String>()
    var mDeliveryDate = Date()
        set(value) {
            mDeliveryDateStr.set(date2Str(value))
        }


    val mPickUpDateType = ObservableInt()
    val mDeliveryDateType = ObservableInt()

    override fun onSave(): Boolean{
        mRequiredField =
            if (mShipDateStr.get().isNullOrEmpty()) AppLogic.requiredEbolFields["shipDate"]
            else if (mPickUpDateStr.get().isNullOrEmpty()) AppLogic.requiredEbolFields["pickUpDate"]
            else if (mDeliveryDateStr.get().isNullOrEmpty()) AppLogic.requiredEbolFields["deliveryDate"]
            else null
        return super.onSave()
    }

    override fun isFormHasChanged(): Boolean{
        mInitialEbol?.run {
            return shipDate != mShipDateStr.get()
                    || pickUpDate != mPickUpDateStr.get()
                    || deliveryDate != mDeliveryDateStr.get()
                    || pickUpDateType != mPickUpDateType.get()
                    || deliveryDateType != mDeliveryDateType.get()
        }
        return false
    }

    fun onTouch(v: View, event: MotionEvent): Boolean{
        resetFocus()
        return false
    }

    private fun date2Str(date: Date): String {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy")
        return dateFormat.format(date)
    }

    private fun str2Date(str: String): Date {
        val dateFormat = SimpleDateFormat("MM-dd-yyyy")
        try {
            return dateFormat.parse(str)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return Date()
    }
}