package com.driver.reliantdispatch.presentation.secondary

interface SelectedColorStorage{
    fun getSelectedColorIndex(): Int

    fun setSelectedColorIndex(ind: Int)
}