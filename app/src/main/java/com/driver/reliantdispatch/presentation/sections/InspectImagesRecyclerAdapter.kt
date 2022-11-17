package com.driver.reliantdispatch.presentation.sections

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.driver.reliantdispatch.databinding.ViewInspectImageBinding
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined

class InspectImagesRecyclerAdapter(
    val mViewModel: ConditionViewModel,
    val mFragment: Fragment
): RecyclerView.Adapter<InspectImagesRecyclerAdapter.ViewHolder>(){

    var mDataset: List<InspectImageJoined> = listOf()
        set(value){
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    class ViewHolder(val binding: ViewInspectImageBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewInspectImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        mDataset[pos].inspectImage?.let {
            holder.binding.fileUrl = it.fileUrl
        }
        holder.binding.imageList = mDataset[pos].damagesList
        holder.binding.pos = pos
        holder.binding.viewModel = mViewModel
        holder.binding.fragment = mFragment
        holder.binding.executePendingBindings()
        //Log.d(LOG_TAG, "bind $mFragment in $this")
    }

    override fun getItemCount(): Int = mDataset.size

}