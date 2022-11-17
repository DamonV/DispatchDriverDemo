package com.driver.reliantdispatch.presentation

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentSettingsBinding
import com.driver.reliantdispatch.presentation.secondary.BaseFragment


class SettingsFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        binding.fragment = this
        return binding.root
    }

    fun onChangePasswordClick(v: View){
        Navigation.findNavController(v).navigate(R.id.nav_change_password)
    }

    fun onReferClick(v: View){
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            val text = resources.getString(R.string.text_share, "https://play.google.com/store/apps/details?id=${context!!.packageName}")
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}