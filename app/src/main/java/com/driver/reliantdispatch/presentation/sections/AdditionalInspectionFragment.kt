package com.driver.reliantdispatch.presentation.sections

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentAdditionalInspectionBinding
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.domain.entities.AdditionalInspection
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException


class AdditionalInspectionFragment: BaseFragment() {
    lateinit var mBinding: ViewDataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(AdditionalInspectionViewModel::class.java)

        mBinding = FragmentAdditionalInspectionBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentAdditionalInspectionBinding) {
            lifecycleOwner = this@AdditionalInspectionFragment
            fragment = this@AdditionalInspectionFragment
            viewModel = mViewModel as AdditionalInspectionViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with (mViewModel as AdditionalInspectionViewModel) {
            if (savedInstanceState != null) {
                val savedJson = savedInstanceState.getString(ARG_1, "")
                try {
                    mAdditionalInspection.set(Gson().fromJson(savedJson, AdditionalInspection::class.java))
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            } else if (mMainViewModel.mAdditionalInspection!=null) {
                mAdditionalInspection.set(mMainViewModel.mAdditionalInspection!!)
            } else mAdditionalInspection.set(AdditionalInspection())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (mViewModel as AdditionalInspectionViewModel).mAdditionalInspection.get()?.let{
            outState.putString(ARG_1, Gson().toJson(it))
        }
    }

    fun onSaveClick() {
        if (onSave()) {
            mMainViewModel.mAdditionalInspection = (mViewModel as AdditionalInspectionViewModel).mAdditionalInspection.get()
            activity?.let {
                Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
            }
        }
    }

    fun onCloseClick() {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.close_confirm)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()

                mMainViewModel.mAdditionalInspection = null
                activity?.let {
                    Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    fun onSave(): Boolean{
        return (mViewModel as SectionViewModel).onSave()
    }

    override fun onBackPressed(): Boolean{
        mMainViewModel.mAdditionalInspection = null
        return false
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
}