package com.driver.reliantdispatch.presentation.sections

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentLoadIdBinding


class LoadIdFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(LoadIdViewModel::class.java)

        mBinding = FragmentLoadIdBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentLoadIdBinding) {
            lifecycleOwner = this@LoadIdFragment
            fragment = this@LoadIdFragment
            viewModel = mViewModel as LoadIdViewModel
        }
        return mBinding.root
    }

}