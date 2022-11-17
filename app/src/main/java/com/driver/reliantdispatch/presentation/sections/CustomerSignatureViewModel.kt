package com.driver.reliantdispatch.presentation.sections

import android.graphics.Bitmap
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.domain.AppLogic
import com.driver.reliantdispatch.domain.CustomerSignatureUseCase
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined


class CustomerSignatureViewModel : SectionViewModel(){

    override var mItem = CustomObservableField<EbolJoined?>()
    var mInitialEbol: Ebol? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.ebol?.run{
                mInitialEbol = this
                mEmailsAmount.set(1)
                customerName?.let { mCustomerName.set(it) }
                customerEmail?.let {mCustomerEmail.set(it) }
                customerEmail2?.let {
                    mCustomerEmail2.set(it)
                    if (it.isNotEmpty()) mEmailsAmount.set(2)
                }
                customerEmail3?.let {
                    mCustomerEmail3.set(it)
                    if (it.isNotEmpty()) mEmailsAmount.set(3)
                }
                customerSignature?.let {
                    val bitmap = mCustomerSignatureUseCase.getSignature(it)
                    mCustomerSignatureExist.set(bitmap != null)
                    mCustomerSignatureBitmap.set(bitmap)
                }
                /*customerSignatureFile?.let {
                    val bitmap = mCustomerSignatureUseCase.getSignature(it)
                    mCustomerSignatureFileExist.set(bitmap!=null)
                    mCustomerSignatureBitmap.set(bitmap)
                }*/
                customerAvailability?.let { mCustomerAvailability.set(it) }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as EbolJoined?)?.ebol?.run{
                customerName = mCustomerName.get()
                customerName = mCustomerName.get()
                customerEmail = mCustomerEmail.get()
                customerEmail2 = mCustomerEmail2.get()
                customerEmail3 = mCustomerEmail3.get()

                if (!mCustomerSignatureExist.get()){
                /*if ((!customerSignature.isNullOrEmpty() && !mCustomerSignatureExist.get())
                    || mNewSignature != null){*/
                    //if (!customerSignatureFile.isNullOrEmpty()) mCustomerSignatureUseCase.deleteSignature(customerSignatureFile!!)
                    customerSignature = mNewSignature
                }

                customerAvailability = mCustomerAvailability.get()
            }
            return value
        }
    }

    val mCustomerAvailability = ObservableBoolean()

    val mCustomerName = ObservableField<String>()
    val mCustomerEmail = ObservableField<String>()
    val mCustomerEmail2 = ObservableField<String>()
    val mCustomerEmail3 = ObservableField<String>()
    val mCustomerSignatureBitmap = ObservableField<Bitmap>()
    var mCustomerSignatureExist = ObservableBoolean(false)
    var mNewSignature: String? = null

    var mEmailsAmount = ObservableInt()

    val mCustomerSignatureUseCase = CustomerSignatureUseCase()

    fun addEmail(){
        val i = mEmailsAmount.get()
        if (i<3) mEmailsAmount.set(i+1)
    }

    fun hideEmail(pos: Int){
        val i = mEmailsAmount.get()
        when (pos){
            2 -> {
                if (i==3) {
                    mCustomerEmail2.set(mCustomerEmail3.get())
                    mCustomerEmail3.set("")
                } else mCustomerEmail2.set("")
            }
            3 -> mCustomerEmail3.set("")
        }
        if (i>1) mEmailsAmount.set(i-1)
    }

    fun onClearSign() {
        mCustomerSignatureExist.set(false)
    }

    fun onSave(bitmap: Bitmap?, isBitmapEmpty: Boolean): Boolean{
        mRequiredField =
            if (mCustomerAvailability.get() && mCustomerName.get().isNullOrEmpty()) AppLogic.requiredEbolFields["customerName"]
            else if (mCustomerAvailability.get() && mCustomerEmail.get().isNullOrEmpty()) AppLogic.requiredEbolFields["customerEmail"]
            else {
                if (!mCustomerSignatureExist.get() && !isBitmapEmpty && bitmap != null){
                    mNewSignature = mCustomerSignatureUseCase.saveSignature(bitmap)
                }
                null
            }
        return super.onSave()
    }

    override fun isFormHasChanged(): Boolean{
        mInitialEbol?.run {
            return customerName != mCustomerName.get()
                    || customerName != mCustomerName.get()
                    || customerEmail != mCustomerEmail.get()
                    || customerEmail2 != mCustomerEmail2.get()
                    || customerEmail3 != mCustomerEmail3.get()
                    || customerAvailability != mCustomerAvailability.get()
        }
        return false
    }
}