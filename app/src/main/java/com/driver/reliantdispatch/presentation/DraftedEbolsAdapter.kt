package com.driver.reliantdispatch.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.ViewEbolRowBinding
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.secondary.EbolsAdapter

class DraftedEbolsAdapter(
    val mViewModel: DraftedViewModel,
    val mActivity: FragmentActivity
): RecyclerView.Adapter<DraftedEbolsAdapter.ViewHolder>(), EbolsAdapter {
    override var mDataset = mutableListOf<EbolJoined>()

    class ViewHolder(val binding: ViewEbolRowBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewEbolRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        /*val nestedListView = binding.root.findViewById<NestedListView>(R.id.nestedListView)
        nestedListView.addHeaderView(View(parent.context))
        nestedListView.adapter = VehiclesSimpleAdapter()*/
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = mDataset[pos]
        item?.let {
            holder.binding.item = item
            holder.binding.pos = pos
            holder.binding.viewModel = mViewModel
            //holder.binding.clickHandler = this
        }
    }

    override fun getItemCount(): Int = mDataset.size

    fun onOpenItem(item: EbolJoined){
        val createViewModel = ViewModelProviders.of(mActivity).get(CreateViewModel::class.java)
        createViewModel.mItem.set(item)
        Navigation.findNavController(mActivity, R.id.nav_host_fragment_main).navigate(R.id.nav_details)
    }

    fun onOpenFile(item: EbolJoined){

    }

    fun onPickUpClick(item: EbolJoined){
        val createViewModel = ViewModelProviders.of(mActivity).get(CreateViewModel::class.java)
        createViewModel.mItem.set(item)
        Navigation.findNavController(mActivity, R.id.nav_host_fragment_main)
            .navigate(R.id.nav_create,
            Bundle().let{
                it.putInt(ARG_1, EbolFragmentType.SUBMIT_PICKUP.ordinal)
                it
        })
    }

    fun onDeleteClick(v: View, item: EbolJoined, pos: Int) {
        val builder = AlertDialog.Builder(v.context, R.style.AlertDialogTheme)
        builder.setTitle(R.string.dialog_title_warning)
            .setMessage(v.resources.getString(R.string.remove_ebol_confirm))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()
                mViewModel.onDeleteClick(item, pos)
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
            .create().show()
    }
}