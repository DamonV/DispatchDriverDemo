package com.driver.reliantdispatch.presentation.secondary

import android.view.View
import com.driver.reliantdispatch.domain.entities.Company

interface CompanyActionFragment {

    fun onClick(v: View, text: String)

    fun onShowMapClick(v: View, company: Company?)

    fun onPhoneClick(v: View, phone: String)

    fun onSMSClick(v: View, phone: String)

    fun onEmailClick(v: View, email: String)
}