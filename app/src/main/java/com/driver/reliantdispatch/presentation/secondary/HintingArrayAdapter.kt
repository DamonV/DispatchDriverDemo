package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HintingArrayAdapter<T>: ArrayAdapter<T>{
    constructor(context: Context, resource: Int) : super(context, resource)
    constructor(context: Context, resource: Int, textViewResourceId: Int) : super(context, resource, textViewResourceId)
    constructor(context: Context, resource: Int, objects: Array<T>) : super(context, resource, objects)
    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: Array<T>) : super(
        context,
        resource,
        textViewResourceId,
        objects
    )
    constructor(context: Context, resource: Int, objects: MutableList<T>) : super(context, resource, objects)
    constructor(context: Context, resource: Int, textViewResourceId: Int, objects: MutableList<T>) : super(
        context,
        resource,
        textViewResourceId,
        objects
    )

    var spinnerHint = ""

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        if (position==0) (view as TextView).hint = spinnerHint
        return view
    }

    companion object {
        @JvmStatic
        fun createFromResource(context: Context, textArrayResId: Int, textViewResId: Int): HintingArrayAdapter<CharSequence> {
            val strings = context.resources.getTextArray(textArrayResId)
            return HintingArrayAdapter<CharSequence>(
                context,
                textViewResId,
                0,
                strings
            )
        }

        fun getInstance(context: Context, list: MutableList<CharSequence>, textViewResId: Int): HintingArrayAdapter<CharSequence> {
            return HintingArrayAdapter<CharSequence>(
                context,
                textViewResId,
                0,
                list
            )
        }
    }
}