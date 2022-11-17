package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView


class NestedListView : ListView {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 4, MeasureSpec.AT_MOST)
        )
        /*super.onMeasure(
            widthMeasureSpec,
            heightMeasureSpec
        )
        Log.d(LOG_TAG, "measure $heightMeasureSpec")*/
    }

    /*fun adjustHeight(){
        var totalHeight = 0
        for (i in 0 until count) {
            val listItem = adapter.getView(i, null, this)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = layoutParams
        params.height = totalHeight + dividerHeight * (adapter.count - 1)
        layoutParams = params
        Log.d(LOG_TAG, "my ${params.height}")
    }*/
}