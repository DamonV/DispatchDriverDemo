package com.driver.reliantdispatch.presentation.secondary

import android.text.Editable
import android.text.InputFilter
import android.widget.EditText
import java.lang.ref.WeakReference

class PhoneTextWatcher(
    val format: String
): CustomTextWatcher() {
    override var editText: EditText? = null
        set(value) {
            value?.let{
                editTextWeakReference = WeakReference(value)
                val maxLength = format.length
                value.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            }
        }
    private var editTextWeakReference: WeakReference<EditText>? = null

    override fun afterTextChanged(s: Editable?) {
        val editText = editTextWeakReference?.get() ?: return
        editText.removeTextChangedListener(this)

        val str = s?.toString()
        if (!str.isNullOrEmpty()) {
            var sel = editText.selectionStart

            var newStr = ""
            var i = 0
            var j = 0
            while (true) {
                if (i >= format.length || j >= str.length) break
                val f = format[i]
                val x = str[j]

                if (f != '#' && x != f) {
                    newStr += f
                    if (j < sel) sel ++
                    i++
                }
                else if (f == '#' && x !in '0'..'9') j++
                else {
                    newStr += str[j++]
                    i++
                }
            }
            editText.setText(newStr)
            if (sel in 0..editText.text.length) {
                editText.setSelection(sel)
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
        fun getInstance(format: String) = PhoneTextWatcher(format)
    }

}