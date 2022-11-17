package com.driver.reliantdispatch.presentation.sections

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentAddInfoBinding

class AddInfoFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(AddInfoViewModel::class.java)

        mBinding = FragmentAddInfoBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentAddInfoBinding) {
            lifecycleOwner = this@AddInfoFragment
            fragment = this@AddInfoFragment
            viewModel = mViewModel as AddInfoViewModel
        }
        return mBinding.root
    }

}