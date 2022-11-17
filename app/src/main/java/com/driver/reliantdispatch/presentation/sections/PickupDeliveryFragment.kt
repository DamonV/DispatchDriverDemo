package com.driver.reliantdispatch.presentation.sections

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.domain.ARG_2
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentPickupDeliveryBinding
import com.driver.reliantdispatch.domain.FRAGMENT_DELIVERY
import com.driver.reliantdispatch.domain.dto.ZipEntryDTO
import com.driver.reliantdispatch.presentation.secondary.AutocompleteArrayAdapter
import kotlinx.android.synthetic.main.fragment_shipper_consignor.*

class PickupDeliveryFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(PickupDeliveryViewModel::class.java)

        mBinding = FragmentPickupDeliveryBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentPickupDeliveryBinding) {
            lifecycleOwner = this@PickupDeliveryFragment
            fragment = this@PickupDeliveryFragment
            viewModel = mViewModel as PickupDeliveryViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val fragType = arguments?.getInt(ARG_2, 0) ?: 0
        (mViewModel as PickupDeliveryViewModel).mFragType = fragType
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = when(fragType) {
            FRAGMENT_DELIVERY -> getString(R.string.label_delivery)
            else -> getString(R.string.label_pick_up)
        }

        val adapterZip = AutocompleteArrayAdapter<ZipEntryDTO>(
            activity!!,
            android.R.layout.simple_dropdown_item_1line
        )
        adapterZip.mFilter = (mViewModel as PickupDeliveryViewModel).mFilterZip
        autocompleteZip.setAdapter(adapterZip)

        val adapterCity = AutocompleteArrayAdapter<ZipEntryDTO>(
            activity!!,
            android.R.layout.simple_dropdown_item_1line
        )
        adapterCity.mFilter = (mViewModel as PickupDeliveryViewModel).mFilterCity
        autocompleteCity.setAdapter(adapterCity)
    }
}