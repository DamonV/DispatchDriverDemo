package com.driver.reliantdispatch.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.ViewEbolArchivedRowBinding
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined


class ArchivedEbolsAdapter(
    val mViewModel: ArchivedViewModel,
    val mActivity: FragmentActivity
): PagedListAdapter<EbolJoined, ArchivedEbolsAdapter.ViewHolder>(diffCallback){

    class ViewHolder(val binding: ViewEbolArchivedRowBinding): RecyclerView.ViewHolder(binding.root){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ViewEbolArchivedRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = getItem(pos)
        holder.binding.item = item
        holder.binding.pos = pos
        holder.binding.clickHandler = this
    }

    fun onOpenItem(item: EbolJoined){
        val createViewModel = ViewModelProviders.of(mActivity).get(CreateViewModel::class.java)
        createViewModel.mItem.set(item)
        Navigation.findNavController(mActivity, R.id.nav_host_fragment_main).navigate(R.id.nav_details)
    }

    fun onOpenFile(item: EbolJoined){

    }

    fun onDeleteClick(v: View, item: EbolJoined, pos: Int) {
        val builder = AlertDialog.Builder(v.context, R.style.AlertDialogTheme)
        builder.setTitle(R.string.dialog_title_warning)
            .setMessage(v.resources.getString(R.string.delete_ebol_confirm))
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

    fun onRestoreClick(v: View, item: EbolJoined, pos: Int) {
        mViewModel.onRestoreClick(item, pos)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<EbolJoined>() {
            override fun areItemsTheSame(oldItem: EbolJoined, newItem: EbolJoined): Boolean =
                oldItem.ebol?.id == newItem.ebol?.id

            override fun areContentsTheSame(oldItem: EbolJoined, newItem: EbolJoined): Boolean =
                oldItem.ebol?.id == newItem.ebol?.id
        }
    }
}