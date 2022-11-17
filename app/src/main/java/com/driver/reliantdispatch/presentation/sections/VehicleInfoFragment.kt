package com.driver.reliantdispatch.presentation.sections

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.reliantdispatch.databinding.FragmentVehicleInfoBinding
import kotlinx.android.synthetic.main.fragment_vehicle_info.*
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.*


class VehicleInfoFragment: BaseSectionFragment() {
    var readOnly: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(VehicleInfoViewModel::class.java)

        mBinding = FragmentVehicleInfoBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentVehicleInfoBinding) {
            lifecycleOwner = this@VehicleInfoFragment
            fragment = this@VehicleInfoFragment
            viewModel = mViewModel as VehicleInfoViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.label_vehicle_info)
        with (mViewModel as VehicleInfoViewModel) {
            mVehicleIndex = arguments?.getInt(ARG_1, -1) ?: -1
            mScannedVin = arguments?.getString(ARG_2) ?: ""
            readOnly = arguments?.getBoolean(ARG_READ_ONLY, false) ?: false
            mReadOnly.set(readOnly)

            super.onActivityCreated(savedInstanceState)

            mMainViewModel = this@VehicleInfoFragment.mMainViewModel

            /*if (savedInstanceState == null && arguments?.getBoolean(ARG_DETAILS, false) == true)
                activity?.let{
                    val detailsViewModel = ViewModelProviders.of(it).get(DetailsViewModel::class.java)
                    (mViewModel as SectionViewModel).mItem.set(detailsViewModel.mItem.get()?.copy())
                }*/

            colorRecyclerView.setHasFixedSize(true)
            colorRecyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            colorRecyclerView.adapter = ExteriorColorsAdapter(mViewModel as VehicleInfoViewModel, readOnly)

            onStart()
        }
        if (readOnly)
            for (i in 0 until parentLayout.childCount) {
                val child = parentLayout.getChildAt(i)
                if (child.id == R.id.buttonLayout) continue
                child.isEnabled = false
                child.isClickable = false
                if (child is ViewGroup) for (j in 0 until child.childCount) {
                    val subchild = child.getChildAt(j)
                    subchild.isEnabled = false
                    subchild.isClickable = false
                }
            }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.action_save)?.isVisible = !readOnly
    }

}