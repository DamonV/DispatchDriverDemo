package com.driver.reliantdispatch.presentation.sections

import android.R
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentShipperConsignorBinding
import com.driver.reliantdispatch.domain.dto.ZipEntryDTO
import com.driver.reliantdispatch.presentation.secondary.AutocompleteArrayAdapter
import kotlinx.android.synthetic.main.fragment_shipper_consignor.*


class ShipperConsignorFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(ShipperConsignorViewModel::class.java)

        mBinding = FragmentShipperConsignorBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentShipperConsignorBinding) {
            lifecycleOwner = this@ShipperConsignorFragment
            fragment = this@ShipperConsignorFragment
            viewModel = mViewModel as ShipperConsignorViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapterZip = AutocompleteArrayAdapter<ZipEntryDTO>(
            activity!!,
            R.layout.simple_dropdown_item_1line
        )
        adapterZip.mFilter = (mViewModel as ShipperConsignorViewModel).mFilterZip
        autocompleteZip.setAdapter(adapterZip)

        val adapterCity = AutocompleteArrayAdapter<ZipEntryDTO>(
            activity!!,
            R.layout.simple_dropdown_item_1line
        )
        adapterCity.mFilter = (mViewModel as ShipperConsignorViewModel).mFilterCity
        autocompleteCity.setAdapter(adapterCity)
    }
}