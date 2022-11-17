package com.driver.reliantdispatch.presentation.secondary

import android.text.TextWatcher
import android.widget.EditText

abstract class CustomTextWatcher: TextWatcher {
    open var editText: EditText? = null
}