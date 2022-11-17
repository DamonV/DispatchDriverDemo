package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout

class WatchingLinearLayout: LinearLayout{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    var mInterceptListener: View.OnTouchListener? = null

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        mInterceptListener?.onTouch(this, ev)
        return false
    }
}