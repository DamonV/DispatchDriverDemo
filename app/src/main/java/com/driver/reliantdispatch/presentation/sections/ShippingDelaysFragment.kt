package com.driver.reliantdispatch.presentation.sections

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentShippingDelaysBinding
import com.driver.reliantdispatch.presentation.secondary.DatePeakerDialog


class ShippingDelaysFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(ShippingDelaysViewModel::class.java)

        mBinding = FragmentShippingDelaysBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentShippingDelaysBinding) {
            lifecycleOwner = this@ShippingDelaysFragment
            fragment = this@ShippingDelaysFragment
            viewModel = mViewModel as ShippingDelaysViewModel
        }
        return mBinding.root
    }

    fun onClickDate1(v: View){
        if (activity!!.supportFragmentManager.findFragmentByTag(DLG_TAG) != null) return
        val datePeakerDialog = DatePeakerDialog()
        datePeakerDialog.listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            (mViewModel as ShippingDelaysViewModel).mPickUpDate = DatePeakerDialog.args2Date(year, month, dayOfMonth)
        }
        datePeakerDialog.arguments = DatePeakerDialog.date2Args((mViewModel as ShippingDelaysViewModel).mPickUpDate)
        datePeakerDialog.show(activity!!.supportFragmentManager, DLG_TAG)
    }

    fun onClickDate2(v: View){
        if (activity!!.supportFragmentManager.findFragmentByTag(DLG_TAG) != null) return
        val datePeakerDialog = DatePeakerDialog()
        datePeakerDialog.listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            (mViewModel as ShippingDelaysViewModel).mDeliveryDate = DatePeakerDialog.args2Date(year, month, dayOfMonth)
        }
        datePeakerDialog.arguments = DatePeakerDialog.date2Args((mViewModel as ShippingDelaysViewModel).mDeliveryDate)
        datePeakerDialog.show(activity!!.supportFragmentManager, DLG_TAG)
    }

}