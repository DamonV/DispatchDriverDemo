package com.driver.reliantdispatch.presentation.dto

import androidx.fragment.app.Fragment

data class GlideFragmentImage(
    val imageUrl: String?,
    val fragment: Fragment
){
    companion object {
        @JvmStatic
        fun newOne(imageUrl: String, fragment: Fragment) = GlideFragmentImage(imageUrl, fragment)
    }
}