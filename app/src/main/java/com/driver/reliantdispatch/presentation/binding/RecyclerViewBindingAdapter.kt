package com.driver.reliantdispatch.presentation.binding

import android.content.res.Resources
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.presentation.secondary.EbolsAdapter
import com.driver.reliantdispatch.presentation.sections.ExteriorColorsAdapter
import com.driver.reliantdispatch.presentation.sections.InspectImagesRecyclerAdapter

object RecyclerViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("ebols_dataset")
    fun setEbolsDataset(recyclerView: RecyclerView, value: MutableList<EbolJoined>?) {
        if (value!=null) {
            val adapter = recyclerView.adapter as EbolsAdapter
            if (value != adapter.mDataset) {
                adapter.mDataset = value
            }
            adapter.notifyDataSetChanged()
        }
    }

    @JvmStatic
    @BindingAdapter("ebols_paged")
    fun setEbolsPagedDataset(recyclerView: RecyclerView, value: PagedList<EbolJoined>?) {
        val adapter = recyclerView.adapter as PagedListAdapter<EbolJoined, RecyclerView.ViewHolder>
        adapter.submitList(value)
    }

    @JvmStatic
    @BindingAdapter("color_values")
    fun setColorValues(recyclerView: RecyclerView, resId: Int?) {
        if (resId!=null) {
            val adapter = recyclerView.adapter as ExteriorColorsAdapter
            try {
                val colorsTypedArr = recyclerView.context.resources.obtainTypedArray(resId)
                val colorsIntArr = IntArray(colorsTypedArr.length())
                for (i in 0 until colorsTypedArr.length()) colorsIntArr[i] = colorsTypedArr.getColor(i, 0)
                adapter.mColors = colorsIntArr
                colorsTypedArr.recycle()
            } catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("color_names")
    fun setColorNames(recyclerView: RecyclerView, resId: Int?) {
        if (resId!=null) {
            val adapter = recyclerView.adapter as ExteriorColorsAdapter
            try {
                adapter.mNames = recyclerView.context.resources.getStringArray(resId)
                //adapter.notifyDataSetChanged()
            } catch (e: Resources.NotFoundException){
                e.printStackTrace()
            }
        }
    }

    @JvmStatic
    @BindingAdapter("images_dataset")
    fun setImagesDataset(recyclerView: RecyclerView, value: List<InspectImageJoined>?) {
        if (value!=null) {
            val adapter = recyclerView.adapter as InspectImagesRecyclerAdapter
            adapter.mDataset = value
        }
    }

}