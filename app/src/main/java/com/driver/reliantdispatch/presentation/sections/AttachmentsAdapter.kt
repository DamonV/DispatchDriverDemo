package com.driver.reliantdispatch.presentation.sections

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.BaseAdapter
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.ViewAttachmentRowBinding
import com.driver.reliantdispatch.domain.entities.Attachment

class AttachmentsAdapter(
    val mFragment: InspectionFragment
): BaseAdapter() {

    var mDataset: List<Attachment> = listOf()
    set(value){
        //if (field != value) {
            field = value
        //}
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ViewAttachmentRowBinding) {
        fun bind(item: Attachment, pos: Int){
            binding.item = item
            binding.pos = pos
            binding.fileIcon = getFileIconDrawable(binding.root.context, item.fileType ?: "doc")
            binding.fragment = mFragment
            binding.executePendingBindings()
        }
    }

    fun getFileIconDrawable(context: Context, type: String): Drawable {
        var resourceId = context.resources.getIdentifier("ic_file_${type.toLowerCase()}","drawable", context.packageName)
        if (resourceId == 0) resourceId = R.drawable.ic_file_doc
        return context.resources.getDrawable(resourceId)
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val binding = ViewAttachmentRowBinding.inflate(LayoutInflater.from(container.context), container, false)
            convertView = binding.root
            val holder = ViewHolder(binding)
            convertView.tag = holder
            holder.bind(getItem(position) as Attachment, position)
        }
        else {
            val hl = convertView.tag as ViewHolder
            hl.bind(getItem(position) as Attachment, position)
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