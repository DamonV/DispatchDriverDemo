package com.driver.reliantdispatch.presentation.secondary


interface CompanyEditViewModel {
    fun onEditSection(navId: Int, field: Int)

    fun onEditSection(navId: Int, field: Int, fragment: Int)
}