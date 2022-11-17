package com.driver.reliantdispatch.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.databinding.FragmentLoginBinding
import com.driver.reliantdispatch.presentation.secondary.BaseFragment

class LoginFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)

        val binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        binding.viewModel = mViewModel as LoginViewModel
        return binding.root
    }

    override fun onTryConnectionAgain(){
        (mViewModel as LoginViewModel).onLogin()
    }
}