package com.driver.reliantdispatch.presentation.secondary

import android.text.Editable
import android.text.SpannableStringBuilder
import android.widget.EditText
import android.text.TextWatcher
import java.lang.ref.WeakReference


class VinTextWatcher(): TextWatcher {
    var editText: EditText? = null
        set(value) {
            value?.let{
                editTextWeakReference = WeakReference(value)
            }
        }
    private var editTextWeakReference: WeakReference<EditText>? = null


    override fun afterTextChanged(s: Editable?) {
        val editText = editTextWeakReference?.get() ?: return
        editText.removeTextChangedListener(this)
        val str = s?.toString()
        if (!str.isNullOrEmpty()){

            var selStart = editText.selectionStart

            val newStr = str.replace("[^0-9A-NPR-Z]".toRegex(), "")

            //editText.setText(newStr)
            /*editText.text.clear()
            editText.append(SpannableStringBuilder(newStr))*/
            /*s.clear()
            s.insert(0, SpannableStringBuilder(newStr))*/
            //editText.append(newStr)

            s.replace(0, s.length, newStr)

            selStart -= str.length - newStr.length

            if (selStart in 0..editText.text.length) {
                editText.setSelection(selStart)
            } else {
                editText.setSelection(editText.text.length)
            }
        }

        editText.addTextChangedListener(this)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    companion object {
        @JvmStatic
        fun getInstance() = VinTextWatcher()
    }
}