package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Filter

class AutocompleteArrayAdapter<T>: ArrayAdapter<T>{
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

    override fun getFilter(): Filter {
        return mFilter ?: super.getFilter()
    }

    var mFilter: Filter? = null
}