package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatSpinner


class ClickableSpinner: AppCompatSpinner{
    constructor(context: Context?) : super(context)
    constructor(context: Context?, mode: Int) : super(context, mode)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, mode: Int)
            : super(context,attrs,defStyleAttr,mode)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, mode: Int, popupTheme: Resources.Theme?)
            : super(context, attrs, defStyleAttr, mode, popupTheme)

    /*init{
        isFocusable = true
        isFocusableInTouchMode = true
    }*/

    override fun performClick(): Boolean {
        clickSpinnerListener?.onClick(this)
        return super.performClick()
    }

    var clickSpinnerListener: View.OnClickListener? = null

    fun setOnClickSpinnerListener(listener: View.OnClickListener?){
        clickSpinnerListener = listener
    }

}