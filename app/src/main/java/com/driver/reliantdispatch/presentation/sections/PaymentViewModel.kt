package com.driver.reliantdispatch.presentation.sections

import android.view.MotionEvent
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.domain.AppLogic
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined

class PaymentViewModel : SectionViewModel(){

    override var mItem = CustomObservableField<EbolJoined?>()
    var mInitialEbol: Ebol? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.ebol?.run{
                mInitialEbol = this
                paymentMethod?.let { mPaymentMethod.set(it) }
                paymentType?.let { mPaymentType.set(it) }
                paymentAmount?.let { mPaymentAmount.set(it) }
                paymentAmountReceived?.let { mPaymentAmountReceived.set(it) }
                paymentDeliveryAmountReceived?.let { mPaymentDeliveryAmountReceived.set(it) }
                comment?.let { mComment.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as EbolJoined?)?.ebol?.run{
                paymentMethod = mPaymentMethod.get()
                paymentType = mPaymentType.get()
                paymentAmount = mPaymentAmount.get()
                paymentAmountReceived = mPaymentAmountReceived.get()
                paymentDeliveryAmountReceived = mPaymentDeliveryAmountReceived.get()
                comment = mComment.get()
            }
            return value
        }
    }

    inner class EditViewObservableField<T>: ObservableField<T>(){
        override fun set(value: T) {
            super.set(value)
            calcPaymentRemaining()
        }
    }

    val mPaymentMethod = ObservableInt()
    val mPaymentType = ObservableInt()
    val mPaymentAmount = EditViewObservableField<String>()
    val mPaymentAmountReceived = EditViewObservableField<String>()
    val mPaymentRemaining = ObservableField<String>()
    val mPaymentDeliveryAmountReceived = EditViewObservableField<String>()
    val mComment = EditViewObservableField<String>()

    override fun onSave(): Boolean{
        mRequiredField =
            if (mPaymentAmount.get().isNullOrEmpty()) AppLogic.requiredEbolFields["paymentAmount"]
            else null
        return super.onSave()
    }

    override fun isFormHasChanged(): Boolean{
        mInitialEbol?.run {
            return paymentMethod != mPaymentMethod.get()
                    || paymentType != mPaymentType.get()
                    || paymentAmount != mPaymentAmount.get()
                    || paymentAmountReceived != mPaymentAmountReceived.get()
                    || paymentDeliveryAmountReceived != mPaymentDeliveryAmountReceived.get()
                    || comment != mComment.get()
        }
        return false
    }

    fun onTouch(v: View, event: MotionEvent): Boolean{
        resetFocus()
        return false
    }

    private fun calcPaymentRemaining() =  mPaymentRemaining.set(
        Ebol.getBalanceRemaining(mPaymentAmount.get(), mPaymentAmountReceived.get())
    )

}