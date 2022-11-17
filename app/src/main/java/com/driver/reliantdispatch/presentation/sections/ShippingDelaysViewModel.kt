package com.driver.reliantdispatch.presentation.sections

import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.entities.Ebol
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ShippingDelaysViewModel : SectionViewModel(){

    override var mItem = CustomObservableField<EbolJoined?>()
    var mInitialEbol: Ebol? = null

    inner class CustomObservableField<T>: ObservableField<T>(){
        override fun set(value: T){
            super.set(value)
            (value as EbolJoined?)?.ebol?.run{
                mInitialEbol = this
                delayedPickUpDate?.let { mPickUpDate = str2Date(it) }
                delayedDeliveryDate?.let { mDeliveryDate = str2Date(it) }
                delayedPickUpDateType?.let { mPickUpDateType.set(it) }
                delayedDeliveryDateType?.let { mDeliveryDateType.set(it) }
                delayReasonsArr()?.let { delayReasons ->
                    mReasons.get()?.let {
                        val arr = it.clone() as ArrayList<Boolean>
                        for (reason in delayReasons) arr[reason] = true
                        mReasons.set(arr)
                    }
                }
            }
        }

        override fun get(): T? {
            val value = super.get()
            (value as EbolJoined?)?.ebol?.run{
                delayedPickUpDate = mPickUpDateStr.get()
                delayedDeliveryDate = mDeliveryDateStr.get()
                delayedPickUpDateType = mPickUpDateType.get()
                delayedDeliveryDateType = mDeliveryDateType.get()
                mReasons.get()?.let {
                    val list = mutableListOf<Int>()
                    for ((i, bool) in it.withIndex()) if (bool) list.add(i)
                    delayReasons = list.joinToString(separator = "|")
                }
            }
            return value
        }
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

    val mReasons = ObservableField<ArrayList<Boolean>>()

    init {
        mReasons.set(ArrayList(Array(6) {false}.toList()))
    }


    fun onTouch(v: View, event: MotionEvent): Boolean{
        resetFocus()
        return false
    }

    fun onCheckReasons(v: View, isChecked: Boolean, pos: Int){
        if (!isChecked) return
        mReasons.get()?.let{
            val checkedAmount = it.filter { it }.size
            if (checkedAmount > 1 && !it[pos])
                (v as CheckBox).isChecked = false
        }
    }

    override fun onSave(): Boolean{
        if (mPickUpDateStr.get().isNullOrEmpty() || mDeliveryDateStr.get().isNullOrEmpty()){
            viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.resources.getString(R.string.error_submit_delay1)
            )
            return false
        }
        else {
            val checkedAmount = mReasons.get()?.let { it.filter { it }.size } ?: 0
            if (checkedAmount <= 0 || checkedAmount > 2) {
                viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    context.resources.getString(R.string.error_submit_delay2)
                )
                return false
            }
        }
        return true
    }

    override fun isFormHasChanged(): Boolean{
        mInitialEbol?.run {

            return delayedPickUpDate != mPickUpDateStr.get()
                    || delayedDeliveryDate != mDeliveryDateStr.get()
                    || delayedPickUpDateType != mPickUpDateType.get()
                    || delayedDeliveryDateType != mDeliveryDateType.get()
                    || delayReasons != mReasons.get()?.let {
                            val list = mutableListOf<Int>()
                            for ((i, bool) in it.withIndex()) if (bool) list.add(i)
                            list.joinToString(separator = "|")
                        }
        }
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