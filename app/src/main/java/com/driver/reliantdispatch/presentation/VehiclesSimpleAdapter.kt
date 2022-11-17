package com.driver.reliantdispatch.presentation

import android.util.Log
import android.view.*
import android.widget.BaseAdapter
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.ViewVehicleSimpleRowBinding
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.RUNNING_NO
import com.driver.reliantdispatch.domain.entities.Vehicle
import com.driver.reliantdispatch.domain.entities.joined.VehicleJoined

open class VehiclesSimpleAdapter: BaseAdapter() {

    var mDataset: List<VehicleJoined> = listOf()
    set(value){
        //if (field != value) {
            field = value
            notifyDataSetChanged()
        //}
    }

    /*fun swapData(newDataset: List<VehicleJoined>) {
        mDataset.clear()
        mDataset.addAll(newDataset)
        notifyDataSetChanged()
    }*/

    inner class ViewHolder(val binding: ViewVehicleSimpleRowBinding) {
        fun bind(item: Vehicle){
            binding.item = item
            binding.vehicleSize = binding.root.context.resources.getString(
                R.string.text_vehicle_size_simple,
                item.length ?: " - ",
                item.width ?: " - ",
                item.height ?: " - ",
                item.curbWeight ?: " - ")
            binding.inopForklift = if (item.running == RUNNING_NO && item.forklift == false)
                    binding.root.context.resources.getString(R.string.text_vehicle_inop)
                else if (item.running == RUNNING_NO && item.forklift == true)
                    binding.root.context.resources.getString(R.string.text_vehicle_inop_forklift)
                else null
            binding.executePendingBindings()
        }
    }

    override fun getView(position: Int, view: View?, container: ViewGroup): View {
        var convertView = view
        if (convertView == null) {
            val binding = ViewVehicleSimpleRowBinding.inflate(LayoutInflater.from(container.context), container, false)
            convertView = binding.root
            val holder = ViewHolder(binding)
            convertView.tag = holder
            holder.bind(getItem(position) as Vehicle)
        }
        else {
            val hl = convertView.tag as ViewHolder
            hl.bind(getItem(position) as Vehicle)
        }
        return convertView
    }

    override fun getCount(): Int {
        return mDataset.size
    }

    override fun getItem(i: Int): Any {
        return mDataset[i].vehicle ?: Vehicle()
    }

    override fun getItemId(i: Int): Long {
        /*val veh = mDataset[i].vehicle?.let{
            ""+it.year+" "+it.make+" "+it.model
        }

        Log.d(LOG_TAG, "item $veh id = ${mDataset[i].vehicle?.id ?: i.toLong()}")*/
        return mDataset[i].vehicle?.id ?: i.toLong()
    }
}