package com.driver.reliantdispatch.presentation.binding

import androidx.databinding.BindingAdapter
import android.text.InputFilter
import android.widget.TextView
import android.text.Spanned
import androidx.databinding.adapters.ListenerUtil
import android.text.TextWatcher
import android.widget.EditText
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.presentation.secondary.CustomTextWatcher
import com.driver.reliantdispatch.presentation.secondary.VinTextWatcher


object TextViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("textWatcher")
    fun setTextWatcher(view: TextView, newValue: TextWatcher?) {
        val oldValue = ListenerUtil.trackListener(view, newValue, R.id.listenerId)
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue)
        }
        if (newValue != null) {
            view.addTextChangedListener(newValue)
        }
    }

    @JvmStatic
    @BindingAdapter("textWatcherNumerical")
    fun setTextWatcherCurrency(view: EditText, newValue: CustomTextWatcher?) {
        val oldValue = ListenerUtil.trackListener(view, newValue, R.id.listenerId)
        if (oldValue != null) {
            view.removeTextChangedListener(oldValue)
        }
        if (newValue != null) {
            newValue.editText = view
            view.addTextChangedListener(newValue)
        }
    }


    @JvmStatic
    @BindingAdapter("maxValue")
    fun setMaxValue(view: TextView, value: Int) {
        var filters: Array<InputFilter>? = view.filters
        if (filters == null) {
            filters = arrayOf(MinMaxFilter(mIntMax = value))
        } else {
            var foundMinMaxFilter = false
            for (i in filters.indices) {
                val filter = filters[i]
                if (filter is MinMaxFilter) {
                    foundMinMaxFilter = true
                    filter.mIntMax = value
                    break
                }
            }
            if (!foundMinMaxFilter) {
                val oldFilters = filters
                filters = Array(oldFilters.size + 1) {
                    if (it < oldFilters.size) oldFilters[it]
                    else MinMaxFilter(mIntMax = value)
                }
            }
        }
        view.filters = filters
    }

    @JvmStatic
    @BindingAdapter("minValue")
    fun setMinValue(view: TextView, value: Int) {
        var filters: Array<InputFilter>? = view.filters
        if (filters == null) {
            filters = arrayOf(MinMaxFilter(mIntMin = value))
        } else {
            var foundMinMaxFilter = false
            for (i in filters.indices) {
                val filter = filters[i]
                if (filter is MinMaxFilter) {
                    foundMinMaxFilter = true
                    filter.mIntMin = value
                    break
                }
            }
            if (!foundMinMaxFilter) {
                val oldFilters = filters
                filters = Array(oldFilters.size + 1) {
                    if (it < oldFilters.size) oldFilters[it]
                    else MinMaxFilter(mIntMin = value)
                }
            }
        }
        view.filters = filters
    }

    @JvmStatic
    @BindingAdapter("vinFilter")
    fun setVinFilter(view: TextView, value: Boolean) {
        if (value) {
            var filters: Array<InputFilter>? = view.filters
            if (filters == null) {
                filters = arrayOf(VinFilter())
            } else {
                var found = false
                for (i in filters.indices) {
                    val filter = filters[i]
                    if (filter is VinFilter) {
                        found = true
                        break
                    }
                }
                if (!found) {
                    val oldFilters = filters
                    filters = Array(oldFilters.size + 1) {
                        if (it < oldFilters.size) oldFilters[it]
                        else VinFilter()
                    }
                }
            }
            view.filters = filters
        }
    }

    @JvmStatic
    @BindingAdapter("vinWatcher")
    fun setVinWatcher(view: EditText, value: Boolean) {
        if (value) {
            val watcher = VinTextWatcher.getInstance()
            val oldValue = ListenerUtil.trackListener(view, watcher, R.id.listenerId)
            if (oldValue != null) {
                view.removeTextChangedListener(oldValue)
            }
            watcher.editText = view
            view.addTextChangedListener(watcher)
        }
    }


    class MinMaxFilter (
        var mIntMin: Int = 0,
        var mIntMax: Int = 0
    ): InputFilter {

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            try {
                val input = (dest.toString() + source.toString()).toInt()
                if (input in mIntMin..mIntMax)
                    return null
            } catch (e: NumberFormatException) {
            }
            return ""
        }
    }

    class VinFilter (): InputFilter {

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int
        ): CharSequence? {

            /*if (dend - dstart == 0 || end - start ==0) return null
            else {*/
                val input = source.subSequence(start, end)
                return input.replace("[[^0-9A-NPR-Z]]".toRegex(), "")
            //}
        }
    }

}