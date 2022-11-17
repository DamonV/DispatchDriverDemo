package com.driver.reliantdispatch.presentation.secondary

import android.text.Editable
import android.widget.EditText
import android.util.Log
import com.driver.reliantdispatch.domain.LOG_TAG
import java.lang.Exception
import java.lang.ref.WeakReference
import java.math.BigDecimal
import java.math.BigDecimal.ROUND_DOWN
import java.text.DecimalFormat
import java.util.Locale


class NumericalTextWatcher(
    val format: String,
    val roundDecimals: Int = 0
): CustomTextWatcher() {
    override var editText: EditText? = null
        set(value) {
            value?.let{
                editTextWeakReference = WeakReference(value)
            }
        }
    private var editTextWeakReference: WeakReference<EditText>? = null

    private var df: DecimalFormat = (DecimalFormat.getInstance(Locale.US) as DecimalFormat).apply{
        applyPattern(format)
    }
    //DecimalFormat(format)
    private val decimalDigits: Int
    private val dot: Char

    init {
        dot = df.decimalFormatSymbols.decimalSeparator
        decimalDigits = format.indexOf('.').let{
            if (it in 0..format.length) format.length - it - 1 else 0
        }
    }

    override fun afterTextChanged(s: Editable?) {
        val editText = editTextWeakReference?.get() ?: return
        editText.removeTextChangedListener(this)
        val str = s?.toString()
        if (!str.isNullOrEmpty()){

            var selStart = editText.selectionStart
            var digitsAmount = if (selStart in 0..str.length) {
                val leftPart = str.substring(0, selStart)
                leftPart.replace("[^0-9^$dot]".toRegex(), "").length
            } else -1

            val number = str.replace("[^0-9^$dot]".toRegex(), "").let{
                try {
                    var strNum = it
                    if (decimalDigits > 0 && it.indexOf(dot) == -1)
                        try {
                            strNum = StringBuilder(it).insert(it.length - decimalDigits - 1, dot).toString()
                        } catch (e: Exception){
                            Log.d(LOG_TAG, "StringBuilder.insert exception in $strNum ${it.length - decimalDigits - 1} ${it.length}")
                        }
                    strNum = strNum.replace("\\$dot".toRegex(), ".")
                    BigDecimal(strNum).setScale(roundDecimals, ROUND_DOWN)
                } catch (e: Exception){
                    e.printStackTrace()
                    BigDecimal(0)
                }
            }

            //number = number.round(roundDecimals)

            val newStr = df.format(number)
            editText.setText(newStr)

            if (digitsAmount > -1)
                for ((i,char) in newStr.withIndex()){
                    if (char in '0'..'9' || char == dot) digitsAmount--
                    if (digitsAmount == 0){
                        selStart = i+1
                        break
                    }
                }

            if (selStart in 0..editText.text.length) {
                editText.setSelection(selStart)
            } else {
                editText.setSelection(editText.text.length)
            }
        }

        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    companion object {
        @JvmStatic
        fun getInstance(format: String, roundDecimals: Int) = NumericalTextWatcher(format, roundDecimals)

        @JvmStatic
        fun getInstance(format: String) = NumericalTextWatcher(format)
    }
}

/*fun Double.round(decimals: Int): Double {
    if (decimals >= 0) {
        val multiplier = 10F.pow(decimals)
        return round(this * multiplier) / multiplier
    } else return this
}*/