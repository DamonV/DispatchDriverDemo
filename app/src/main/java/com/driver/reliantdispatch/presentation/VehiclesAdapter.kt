package com.driver.reliantdispatch.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.ViewVehicleRowBinding
import com.driver.reliantdispatch.domain.*
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.Vehicle
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.domain.entities.joined.VehicleJoined


class VehiclesAdapter(
    val viewModel: CreateViewModel,
    val isDetails: Boolean = false
): BaseAdapter() {

    var mDataset: List<VehicleJoined> = listOf()
    set(value){
        //if (field != value) {
            field = value
            notifyDataSetChanged()
        //}
    }

    inner class ViewHolder(val binding: ViewVehicleRowBinding) {
        fun bind(itemJoined: VehicleJoined, pos: Int){
            val context = binding.root.context
            val item = itemJoined.vehicle ?: Vehicle()
            binding.item = item
            binding.vehicleSize = context.resources.getString(R.string.text_vehicle_size,
                item.length ?: " - ",
                item.width ?: " - ",
                item.height ?: " - ",
                item.curbWeight ?: " - ",
                item.vin ?: " - ")
            binding.damagesStr = InspectImageJoined.damagesString(
                itemJoined.pickupInspect?.imagesList?.let {
                    if (it.size > 0) it[0]
                    else null
                })
            binding.pos = pos
            binding.clickHandler = this@VehiclesAdapter
            binding.executePendingBindings()
        }
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val binding = ViewVehicleRowBinding.inflate(LayoutInflater.from(container.context), container, false)
            convertView = binding.root
            val holder = ViewHolder(binding)
            convertView.tag = holder
            holder.bind(getItem(position) as VehicleJoined, position)
        }
        else {
            val hl = convertView.tag as ViewHolder
            hl.bind(getItem(position) as VehicleJoined, position)
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

    @SuppressLint("RestrictedApi")
    fun onClickMenu(v: View, item: Vehicle, pos: Int) {
        val menuBuilder = MenuBuilder(v.context)

        val menuResId = if (!isDetails) when (viewModel.mItem.get()?.ebol?.status ?: 0) {
            EbolStatus.DRAFTED.ordinal -> R.menu.vehicle_popup
            EbolStatus.ASSIGNED.ordinal -> R.menu.vehicle_popup_assigned
            EbolStatus.PICKED_UP.ordinal -> R.menu.vehicle_popup_pickedup
            else -> R.menu.vehicle_popup_delivered
        } else when (viewModel.mItem.get()?.ebol?.status ?: 0) {
            EbolStatus.DRAFTED.ordinal,
            EbolStatus.ASSIGNED.ordinal,
            EbolStatus.PICKED_UP.ordinal -> R.menu.vehicle_readonly_popup
            else -> R.menu.vehicle_popup_delivered
        }

        MenuInflater(v.context).inflate(menuResId, menuBuilder)
        val optionsMenu = MenuPopupHelper(v.context, menuBuilder, v)
        optionsMenu.setForceShowIcon(true)
        menuBuilder.setCallback(object : MenuBuilder.Callback {
            override fun onMenuItemSelected(menu: MenuBuilder, menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.action_edit_vehicle -> {
                        val args = Bundle()
                        args.putInt(ARG_1, pos)
                        Navigation.findNavController(v).navigate(R.id.nav_vehicle_info, args)
                    }
                    R.id.action_view_vehicle -> {
                        val args = Bundle()
                        args.putInt(ARG_1, pos)
                        args.putBoolean(ARG_READ_ONLY, true)
                        //args.putBoolean(ARG_DETAILS, (viewModel is DetailsViewModel))
                        Navigation.findNavController(v).navigate(R.id.nav_vehicle_info, args)
                    }
                    R.id.action_edit_pickup_inspect -> {
                        val args = Bundle()
                        args.putInt(ARG_1, pos)
                        args.putInt(ARG_2, PICKUP_INSPECTION)
                        Navigation.findNavController(v).navigate(R.id.nav_inspection, args)
                    }
                    R.id.action_view_pickup_inspect -> {
                        val args = Bundle()
                        args.putInt(ARG_1, pos)
                        args.putInt(ARG_2, PICKUP_INSPECTION)
                        args.putBoolean(ARG_READ_ONLY, true)
                        //args.putBoolean(ARG_DETAILS, (viewModel is DetailsViewModel))
                        Navigation.findNavController(v).navigate(R.id.nav_inspection, args)
                    }
                    R.id.action_edit_delivery_inspect -> {
                        val args = Bundle()
                        args.putInt(ARG_1, pos)
                        args.putInt(ARG_2, DELIVERY_INSPECTION)
                        Navigation.findNavController(v).navigate(R.id.nav_inspection, args)
                    }
                    R.id.action_view_delivery_inspect -> {
                        val args = Bundle()
                        args.putInt(ARG_1, pos)
                        args.putInt(ARG_2, DELIVERY_INSPECTION)
                        args.putBoolean(ARG_READ_ONLY, true)
                        //args.putBoolean(ARG_DETAILS, (viewModel is DetailsViewModel))
                        Navigation.findNavController(v).navigate(R.id.nav_inspection, args)
                    }
                    R.id.action_remove -> {
                        AlertDialog.Builder(v.context, R.style.AlertDialogTheme)
                        .setTitle(R.string.dialog_title_warning)
                            .setMessage(v.resources.getString(R.string.remove_vehicle_confirm))
                            .setPositiveButton(
                                android.R.string.ok
                            ) { dialog, _ ->
                                dialog.cancel()
                                viewModel.removeVehicle(pos)
                            }
                            .setNegativeButton(
                                android.R.string.cancel
                            ) { dialog, _ -> dialog.cancel() }
                            .create().show()
                    }
                }
                return true
            }
            override fun onMenuModeChange(menu: MenuBuilder?) {
            }
        })
        optionsMenu.show()
    }

}