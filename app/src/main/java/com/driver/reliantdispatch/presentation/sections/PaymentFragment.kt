package com.driver.reliantdispatch.presentation.sections

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentPaymentBinding
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.domain.ARG_2


class PaymentFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(PaymentViewModel::class.java)

        mBinding = FragmentPaymentBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentPaymentBinding) {
            lifecycleOwner = this@PaymentFragment
            fragment = this@PaymentFragment
            viewModel = mViewModel as PaymentViewModel
        }
        return mBinding.root
    }

}