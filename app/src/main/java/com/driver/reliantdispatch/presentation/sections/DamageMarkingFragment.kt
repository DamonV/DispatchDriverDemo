package com.driver.reliantdispatch.presentation.sections

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.databinding.FragmentDamageMarkingBinding
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_damage_marking.*


class DamageMarkingFragment: BaseFragment() {
    lateinit var mBinding: ViewDataBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(DamageMarkingViewModel::class.java)

        mBinding = FragmentDamageMarkingBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentDamageMarkingBinding) {
            lifecycleOwner = this@DamageMarkingFragment
            fragment = this@DamageMarkingFragment
            viewModel = mViewModel as DamageMarkingViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        with (mViewModel as DamageMarkingViewModel) {
            mInspectionType = arguments?.getInt(ARG_1, -1) ?: -1

            super.onActivityCreated(savedInstanceState)

            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

            if (savedInstanceState != null){
                val savedJson = savedInstanceState.getString(ARG_1, "")
                try {
                    mInspectImage = Gson().fromJson(savedJson, InspectImageJoined::class.java)
                } catch(e: JsonSyntaxException) {e.printStackTrace()}
            } else mMainViewModel.mInspectImage?.let {
                mInspectImage = it
            }

            onStart()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (mViewModel as DamageMarkingViewModel).mInspectImage?.let{
            outState.putString(ARG_1, Gson().toJson(it))
        }
    }

    fun onSaveClick() {
        /*val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.save_confirm)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()
                //mMainViewModel.mInspectImage = (mViewModel as DamageMarkingViewModel).mInspectImage
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()*/

        mMainViewModel.mInspectImage?.let{
            if (damageLayerView.damagesList!=null) it.damagesList = damageLayerView.damagesList!!
        }

        activity?.let {
            Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
        }

    }

    fun onClearLastClick() {
        damageLayerView.removeLast()
        /*mMainViewModel.mInspectImage = null
        activity?.let {
            Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
        }*/
    }

    fun onNoDamageClick() {
        damageLayerView.removeAll()
    }

    override fun onBackPressed(): Boolean{
        if ((mViewModel as SectionViewModel).isFormHasChanged()){
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.close_confirm)
                .setPositiveButton(
                    android.R.string.ok
                ) { dialog, _ ->
                    dialog.cancel()
                    mMainViewModel.mInspectImage = null
                    activity?.let {
                        Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
                    }
                }
                .setNegativeButton(
                    android.R.string.cancel
                ) { dialog, _ ->
                    dialog.cancel()
                }
            val dialog = builder.create()
            dialog.show()
            return true
        } else {
            mMainViewModel.mInspectImage = null
            return false
        }
    }
}