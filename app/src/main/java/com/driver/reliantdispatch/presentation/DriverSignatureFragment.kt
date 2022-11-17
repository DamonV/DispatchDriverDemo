package com.driver.reliantdispatch.presentation

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentDriverSignatureBinding
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import kotlinx.android.synthetic.main.fragment_driver_signature.nestedScrollView
import kotlinx.android.synthetic.main.fragment_driver_signature.signatureView


class DriverSignatureFragment: BaseFragment() {
    lateinit var mBinding: FragmentDriverSignatureBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        mViewModel = ViewModelProviders.of(this).get(DriverSignatureViewModel::class.java)

        mBinding = FragmentDriverSignatureBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        mBinding.viewModel = mViewModel as DriverSignatureViewModel
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        signatureView.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Disable the scroll view to intercept the touch event
                        nestedScrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }
                    MotionEvent.ACTION_UP -> {
                        // Allow scroll view to intercept the touch event
                        nestedScrollView.requestDisallowInterceptTouchEvent(false)
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        nestedScrollView.requestDisallowInterceptTouchEvent(true)
                        return false
                    }
                    else -> return true
                }
            }
        })

        (mViewModel as DriverSignatureViewModel).mMainViewModel = mMainViewModel
    }

    fun onClearSign(){
        signatureView.clearCanvas()
        (mViewModel as DriverSignatureViewModel).onClearSign()
    }

    fun onSaveClick(){
        (mViewModel as DriverSignatureViewModel).onSave(signatureView.signatureBitmap, signatureView.isBitmapEmpty)
    }

    fun onCloseClick() {
        activity?.let {
            Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
        }

        /*val builder = AlertDialog.Builder(context!!)
        builder.setTitle(R.string.dialog_title_warning).setMessage(R.string.close_confirm)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()

                activity?.let {
                    Navigation.findNavController(it, R.id.nav_host_fragment_main).navigateUp()
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
        val dialog = builder.create()
        dialog.show()*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val inputManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(mBinding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onBackPressed(): Boolean {
        onCloseClick()
        return true
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

}