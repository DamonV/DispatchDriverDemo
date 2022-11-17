package com.driver.reliantdispatch.presentation.binding

import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.databinding.BindingAdapter

object HtmlTextViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("html_text_clickable")
    fun setClickableHtmlText(view: TextView, text: String) {
        view.movementMethod = LinkMovementMethod.getInstance()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            view.text = Html.fromHtml(text)
        }
    }

    @JvmStatic
    @BindingAdapter("html_text")
    fun setHtmlText(view: TextView, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            view.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT)
        } else {
            view.text = Html.fromHtml(text)
        }
    }
}