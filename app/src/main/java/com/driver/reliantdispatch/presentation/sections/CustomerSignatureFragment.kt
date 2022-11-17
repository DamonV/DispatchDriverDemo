package com.driver.reliantdispatch.presentation.sections

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentCustomerSignatureBinding
import kotlinx.android.synthetic.main.fragment_customer_signature.*
import android.view.MotionEvent


class CustomerSignatureFragment: BaseSectionFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(CustomerSignatureViewModel::class.java)

        mBinding = FragmentCustomerSignatureBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentCustomerSignatureBinding) {
            lifecycleOwner = this@CustomerSignatureFragment
            fragment = this@CustomerSignatureFragment
            viewModel = mViewModel as CustomerSignatureViewModel
        }
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
    }

    fun onClearSign(){
        signatureView.clearCanvas()
        (mViewModel as CustomerSignatureViewModel).onClearSign()
    }

    override fun onSave(): Boolean{
        return (mViewModel as CustomerSignatureViewModel).onSave(signatureView.signatureBitmap, signatureView.isBitmapEmpty)
    }
}