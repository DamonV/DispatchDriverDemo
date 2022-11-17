package com.driver.reliantdispatch.presentation.secondary

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.driver.reliantdispatch.presentation.dto.EventType
import android.content.ActivityNotFoundException
import android.content.Context
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.presentation.MainViewModel
import kotlinx.android.synthetic.main.fragment_create.*
import java.lang.Exception


open class BaseFragment: Fragment() {
    lateinit var mViewModel: ScopedViewModel
    lateinit var mMainViewModel: MainViewModel
    private var mProgressDialog:ProgressDialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.let {
            mMainViewModel = ViewModelProviders.of(it).get(MainViewModel::class.java)
        }

        if (this::mViewModel.isInitialized)
            mViewModel.viewModelEvent.observe(this, Observer {
                when (it.type){
                    EventType.NAVIGATE -> activity?.let { activity ->
                        Navigation.findNavController(activity, R.id.nav_host_fragment_main)
                            .navigate(it.navDest, it.args)
                    }

                    EventType.NAVIGATE_BOTTOM -> activity?.let { activity ->
                        mMainViewModel.mNavBottomDestination = it.navDest
                        try {
                            Navigation.findNavController(activity, R.id.nav_host_fragment_bottom).navigate(it.navDest)
                        } catch (e: IllegalArgumentException){
                            Navigation.findNavController(activity, R.id.nav_host_fragment_main)
                                .popBackStack(R.id.nav_ebols, false)
                        }
                    }

                    EventType.NAVIGATE_UP -> activity?.let { activity ->
                        Navigation.findNavController(activity, R.id.nav_host_fragment_main)
                            .navigateUp()
                    }

                    EventType.NO_INTERNET -> activity?.let { activity ->
                        mMainViewModel.isNoInternet = true
                        Navigation.findNavController(activity, R.id.nav_host_fragment_main)
                            .navigate(R.id.nav_no_internet)
                    }

                    EventType.SHOW_DIALOG -> showAlertDialog(it.dialogTitleResId, it.dialogMsg)

                    EventType.SHOW_TOAST -> {
                        Toast.makeText(context, it.dialogMsg, Toast.LENGTH_LONG).show()
                    }

                    EventType.INTENT -> {
                        try {
                            startActivity(it.intent)
                        } catch (e: ActivityNotFoundException){
                            showAlertDialog(R.string.dialog_title_error, getString(R.string.error_noactivity))
                        } catch (e: Exception){
                            Log.d(LOG_TAG, e.message)
                        }
                    }

                    EventType.REPEAT_ACTION -> activity?.let { activity ->
                        AlertDialog.Builder(activity)
                            .setTitle(it.dialogTitleResId)
                            .setMessage(it.dialogMsg)
                            .setPositiveButton(
                                R.string.button_repeat
                            ) { dialog, _ ->
                                dialog.cancel()
                                onRepeatAction()
                            }
                            .setNegativeButton(
                                android.R.string.cancel
                            ) { dialog, _ ->
                                dialog.cancel()
                                onCancelRepeatAction()
                            }
                            .create().show()
                    }

                    EventType.SHOW_PROGRESS -> mProgressDialog = ProgressDialog.show(
                        context,
                        null,
                        resources.getString(R.string.dialog_msg_progress)
                    )

                    EventType.DISMISS_PROGRESS -> mProgressDialog?.dismiss()

                }
            })

        if (mMainViewModel.isNoInternet) {
            onTryConnectionAgain()
            mMainViewModel.isNoInternet = false
        }
    }

    open fun onTryConnectionAgain(){
    }

    open fun onRepeatAction(){
    }

    open fun onCancelRepeatAction(){

    }

    internal fun showAlertDialog(titleResId: Int, msg: String?) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(titleResId).setMessage(msg)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()
    }

    open fun onClickSpinner(view: View){
        val inputManager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        parentLayout.clearFocus()
    }

    open fun onBackPressed(): Boolean = false       //false - we don't handle the press and allow to run standard actions, true - only our handling
}