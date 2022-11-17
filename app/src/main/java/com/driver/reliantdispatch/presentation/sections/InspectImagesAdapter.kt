package com.driver.reliantdispatch.presentation.sections

import android.view.*
import android.widget.BaseAdapter
import com.driver.reliantdispatch.databinding.ViewInspectImageRowBinding
import com.driver.reliantdispatch.domain.entities.InspectImage
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined

class InspectImagesAdapter(
    val mFragment: InspectionFragment,
    var mInspectionType: Int = -1
): BaseAdapter() {

    var mDataset: MutableList<InspectImageJoined> = mutableListOf()
    set(value){
        //if (field != value) {
            field = value
            notifyDataSetChanged()
        //}
    }

    inner class ViewHolder(val binding: ViewInspectImageRowBinding) {
        fun bind(item: InspectImageJoined, pos: Int){
            binding.item = item.inspectImage ?: InspectImage()
            binding.damagesList = item.damagesList
            binding.inspectionType = mInspectionType
            binding.pos = pos
            binding.fragment = mFragment
            binding.executePendingBindings()
        }
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val binding = ViewInspectImageRowBinding.inflate(LayoutInflater.from(container.context), container, false)
            convertView = binding.root
            val holder = ViewHolder(binding)
            convertView.tag = holder
            holder.bind(getItem(position) as InspectImageJoined, position)
        }
        else {
            val hl = convertView.tag as ViewHolder
            hl.bind(getItem(position) as InspectImageJoined, position)
        }
        return convertView
    }

    override fun getCount(): Int {
        return mDataset.size
    }

    override fun getItem(i: Int): Any {
        return mDataset[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }
}