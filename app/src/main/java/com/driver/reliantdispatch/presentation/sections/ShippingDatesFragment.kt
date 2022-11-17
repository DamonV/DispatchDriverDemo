package com.driver.reliantdispatch.presentation.sections

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentShippingDatesBinding
import com.driver.reliantdispatch.presentation.secondary.DatePeakerDialog


const val DLG_TAG = "datepeak"

class ShippingDatesFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(ShippingDatesViewModel::class.java)

        mBinding = FragmentShippingDatesBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentShippingDatesBinding) {
            lifecycleOwner = this@ShippingDatesFragment
            fragment = this@ShippingDatesFragment
            viewModel = mViewModel as ShippingDatesViewModel
        }
        return mBinding.root
    }

    fun onClickDate1(v: View){
        if (activity!!.supportFragmentManager.findFragmentByTag(DLG_TAG) != null) return
        val datePeakerDialog = DatePeakerDialog()
        datePeakerDialog.listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            (mViewModel as ShippingDatesViewModel).mShipDate = DatePeakerDialog.args2Date(year, month, dayOfMonth)
        }
        datePeakerDialog.arguments = DatePeakerDialog.date2Args((mViewModel as ShippingDatesViewModel).mShipDate)
        datePeakerDialog.show(activity!!.supportFragmentManager, DLG_TAG)
    }

    fun onClickDate2(v: View){
        if (activity!!.supportFragmentManager.findFragmentByTag(DLG_TAG) != null) return
        val datePeakerDialog = DatePeakerDialog()
        datePeakerDialog.listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            (mViewModel as ShippingDatesViewModel).mPickUpDate = DatePeakerDialog.args2Date(year, month, dayOfMonth)
        }
        datePeakerDialog.arguments = DatePeakerDialog.date2Args((mViewModel as ShippingDatesViewModel).mPickUpDate)
        datePeakerDialog.show(activity!!.supportFragmentManager, DLG_TAG)
    }

    fun onClickDate3(v: View){
        if (activity!!.supportFragmentManager.findFragmentByTag(DLG_TAG) != null) return
        val datePeakerDialog = DatePeakerDialog()
        datePeakerDialog.listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            (mViewModel as ShippingDatesViewModel).mDeliveryDate = DatePeakerDialog.args2Date(year, month, dayOfMonth)
        }
        datePeakerDialog.arguments = DatePeakerDialog.date2Args((mViewModel as ShippingDatesViewModel).mDeliveryDate)
        datePeakerDialog.show(activity!!.supportFragmentManager, DLG_TAG)
    }
}