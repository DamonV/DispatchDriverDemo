package com.driver.reliantdispatch.presentation.binding

import android.content.Context
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.BindingAdapter

object ViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("focused")
    fun setFocused(view: View, focused: Boolean) {
        if (view.isFocused != focused) {
            if (focused) {
                view.requestFocus()
            }
            else {
                view.clearFocus()
                val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("focused")
    fun setEditTextFocused(view: EditText, focused: Boolean) {
        if (view.isFocused != focused) {
            if (focused) {
                view.requestFocus()
                if (view.inputType != InputType.TYPE_NULL) {
                    val inputManager =
                        view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
                }
            }
            else {
                view.clearFocus()
                val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    @JvmStatic
    @BindingAdapter("focusable")
    fun setFocusable(view: View, focusable: Boolean) {
        view.isFocusable = focusable
        view.isFocusableInTouchMode = focusable
    }

}