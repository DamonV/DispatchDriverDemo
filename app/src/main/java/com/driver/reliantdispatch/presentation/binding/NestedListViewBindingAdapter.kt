package com.driver.reliantdispatch.presentation.binding

import android.widget.ListView
import androidx.databinding.BindingAdapter
import com.driver.reliantdispatch.domain.entities.Attachment
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.domain.entities.joined.VehicleJoined
import com.driver.reliantdispatch.presentation.secondary.LinearLayoutList
import com.driver.reliantdispatch.presentation.VehiclesAdapter
import com.driver.reliantdispatch.presentation.VehiclesSimpleAdapter
import com.driver.reliantdispatch.presentation.sections.AttachmentsAdapter
import com.driver.reliantdispatch.presentation.sections.InspectImagesAdapter


object NestedListViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("vehicles_dataset")
    fun setVehiclesDataset(view: ListView, value: List<VehicleJoined>?) {
        if (value!=null) {
            val adapter = view.adapter as? VehiclesAdapter
            adapter?.mDataset = value
        }
    }

    @JvmStatic
    @BindingAdapter("vehicles_simple_dataset")
    fun setVehiclesSimpleDataset(view: ListView, value: List<VehicleJoined>?) {
        if (value!=null) {
            val adapter = VehiclesSimpleAdapter()/*when (view.adapter) {
                is VehiclesSimpleAdapter -> view.adapter as VehiclesSimpleAdapter
                else -> {
                    view.adapter = VehiclesSimpleAdapter()
                    view.adapter as VehiclesSimpleAdapter
                }
            }*/
            //adapter.swapData(value)
            adapter.mDataset = value
            view.adapter = adapter
        }
    }

    @JvmStatic
    @BindingAdapter("images_dataset")
    fun setInspectImagesDataset(list: LinearLayoutList, value: MutableList<InspectImageJoined>?) {
        if (value!=null) {
            val adapter = list.adapter as? InspectImagesAdapter
            adapter?.mDataset = value
        }
    }

    @JvmStatic
    @BindingAdapter("attachment_dataset")
    fun setAttachmentDataset(view: ListView, value: List<Attachment>?) {
        if (value!=null) {
            val adapter = view.adapter as? AttachmentsAdapter
            adapter?.mDataset = value
        }
    }
}