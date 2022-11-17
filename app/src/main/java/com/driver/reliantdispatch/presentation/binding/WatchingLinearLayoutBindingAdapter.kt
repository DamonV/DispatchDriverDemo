package com.driver.reliantdispatch.presentation.binding

import android.view.View
import androidx.databinding.BindingAdapter
import com.driver.reliantdispatch.presentation.secondary.WatchingLinearLayout

object WatchingLinearLayoutBindingAdapter{
    @JvmStatic
    @BindingAdapter("onIntercept")
    fun setFocused(view: WatchingLinearLayout, listener: View.OnTouchListener?) {
        view.mInterceptListener = listener
    }
}