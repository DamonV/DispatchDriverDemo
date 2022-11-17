package com.driver.reliantdispatch.presentation.binding

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods
import com.driver.reliantdispatch.presentation.secondary.ClickableSpinner
import com.driver.reliantdispatch.presentation.secondary.HintingArrayAdapter

@BindingMethods(
    BindingMethod(type = ClickableSpinner::class, attribute = "onClickSpinner", method = "setOnClickSpinnerListener")
)
object SpinnerBindingAdapter {
    @JvmStatic
    @BindingAdapter("string_array")
    fun setStringArray(spinner: Spinner, resId: Int?) {
        if (resId!=null) {
            HintingArrayAdapter.createFromResource(
                spinner.context,
                resId,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
    }

    @JvmStatic
    @BindingAdapter("dataset")
    fun setDataset(spinner: Spinner, list: MutableList<out CharSequence>?) {
        if (list!=null) {
            HintingArrayAdapter.getInstance(
                spinner.context,
                list as MutableList<CharSequence>,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }
    }

    @JvmStatic
    @BindingAdapter("spinner_hint")
    fun setSpinnerHint(spinner: Spinner, hint: String?) {
        if (hint!=null) {
            if (spinner.adapter is HintingArrayAdapter<*>) {
                (spinner.adapter as HintingArrayAdapter<*>).spinnerHint = hint
            }
        }
    }

}