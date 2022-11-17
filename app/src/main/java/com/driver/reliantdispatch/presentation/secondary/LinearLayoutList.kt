package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import android.database.DataSetObserver
import android.util.AttributeSet
import android.widget.BaseAdapter
import android.widget.LinearLayout

class LinearLayoutList: LinearLayout {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var mDataSetObserver: DataSetObserver = object : DataSetObserver(){
        override fun onChanged(){
            updateView(adapter)
        }
    }

    var adapter: BaseAdapter? = null
        set(value) {
            field?.unregisterDataSetObserver(mDataSetObserver)
            if (value != null) {
                value.registerDataSetObserver(mDataSetObserver)
                updateView(value)
            }
            field = value
        }

    private fun updateView(adapter: BaseAdapter?){
        adapter?.let { adapter ->
            removeAllViews()
            val size = adapter.count
            for (i in 0 until size) {
                addView(adapter.getView(i, null, this@LinearLayoutList))
            }
        }
    }
}