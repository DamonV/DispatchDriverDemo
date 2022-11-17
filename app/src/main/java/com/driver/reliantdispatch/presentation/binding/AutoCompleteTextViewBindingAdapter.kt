package com.driver.reliantdispatch.presentation.binding

import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import com.driver.reliantdispatch.domain.dto.ZipEntryDTO
import com.driver.reliantdispatch.presentation.secondary.AutocompleteArrayAdapter

object AutoCompleteTextViewBindingAdapter {
    @JvmStatic
    @BindingAdapter("zip_dataset")
    fun setZipDataset(view: AutoCompleteTextView, list: List<ZipEntryDTO>?) {
        val adapter = view.adapter as AutocompleteArrayAdapter<ZipEntryDTO>
        adapter.clear()
        if (list!=null) adapter.addAll(list)
    }

}