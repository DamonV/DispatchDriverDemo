package com.driver.reliantdispatch.presentation.sections

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.navigation.Navigation
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.ARG_FIELD
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.CreateViewModel


open class BaseSectionFragment: BaseFragment() {
    lateinit var mCreateViewModel: CreateViewModel
    lateinit var mBinding: ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let{
            mCreateViewModel = ViewModelProviders.of(it).get(CreateViewModel::class.java)

            //if ((mViewModel as SectionViewModel).mItem.get() == null)
            if (savedInstanceState == null)
                (mViewModel as SectionViewModel).mItem.set(mCreateViewModel.mItem.get()?.copy())
        }
        val field = arguments?.getInt(ARG_FIELD, -1) ?: -1
        (mViewModel as SectionViewModel).setFocus(field)
    }

    open fun onSaveClick() {
        if (onSave()) {
            (mViewModel as SectionViewModel).mItem.get()?.let {
                mCreateViewModel.mItem.set(it)
            }

            activity?.let {
                Navigation.findNavController(it, R.id.nav_host_fragment_main)
                    .popBackStack(R.id.nav_create, false)
            }
        }
    }

    fun onCloseClick() {
        if ((mViewModel as SectionViewModel).isFormHasChanged()){
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.close_confirm)
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, _ ->
                    dialog.cancel()

                    activity?.let {
                        if(!Navigation.findNavController(it, R.id.nav_host_fragment_main).popBackStack(R.id.nav_create, false))
                            Navigation.findNavController(it, R.id.nav_host_fragment_main).popBackStack(R.id.nav_details, false)
                    }
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ -> dialog.cancel() }
            val dialog = builder.create()
            dialog.show()
        } else activity?.let {
            if(!Navigation.findNavController(it, R.id.nav_host_fragment_main).popBackStack(R.id.nav_create, false))
                Navigation.findNavController(it, R.id.nav_host_fragment_main).popBackStack(R.id.nav_details, false)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                onSaveClick()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(mBinding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    open fun onSave(): Boolean{
        return (mViewModel as SectionViewModel).onSave()
    }

    override fun onBackPressed(): Boolean {
        onCloseClick()
        return true
    }
}