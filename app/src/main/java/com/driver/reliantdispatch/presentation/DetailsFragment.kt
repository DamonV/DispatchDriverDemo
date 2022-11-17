package com.driver.reliantdispatch.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.driver.reliantdispatch.databinding.FragmentDetailsBinding
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.domain.entities.Company
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.secondary.CompanyActionFragment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_create.*


class DetailsFragment: BaseFragment(), CompanyActionFragment {
    lateinit var mBinding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activity?.let {
            mViewModel = ViewModelProviders.of(it).get(CreateViewModel::class.java)
            mBinding.viewModel = mViewModel as CreateViewModel
        }

        super.onActivityCreated(savedInstanceState)
        nestedListView.adapter = VehiclesAdapter(mViewModel as CreateViewModel, true)

        with (mViewModel as CreateViewModel) {
            if (savedInstanceState != null) {
                val savedJson = savedInstanceState.getString(ARG_1, "")
                try {
                    mItem.set(Gson().fromJson(savedJson, EbolJoined::class.java))
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                }
            /*} else if (mMainViewModel.mEbol!=null) {
                mItem.set(mMainViewModel.mEbol!!)*/
            } else if (mItem.get() == null) mItem.set(EbolJoined())
            onStart()
        }
        nestedScrollView.scrollTo(0, (mViewModel as CreateViewModel).mScrollPosition)
    }

    override fun onStop() {
        super.onStop()
        (mViewModel as CreateViewModel).mScrollPosition = nestedScrollView.scrollY
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        (mViewModel as CreateViewModel).mItem.get()?.let{
            outState.putString(ARG_1, Gson().toJson(it))
        }
    }

    override fun onClick(v: View, text: String) {
        Toast.makeText(context, "open section ${text}", Toast.LENGTH_LONG).show()
    }

    override fun onShowMapClick(v: View, company: Company?) {
        company?.let {
            val address = Company.addressFull(it)
            if (address.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$address"))
                intent.setPackage("com.google.android.apps.maps")
                if (intent.resolveActivity(v.context.packageManager) != null) {
                    startActivity(intent)
                }
            }
        }
    }

    override fun onPhoneClick(v: View, phone: String){
        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
            Intent(Intent.ACTION_CALL, Uri.parse("tel:$phone")).also{
                startActivity(it)
            }
    }

    override fun onSMSClick(v: View, phone: String){
        Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null)).also{
            startActivity(it)
        }
    }

    override fun onEmailClick(v: View, email: String){
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        }.also{
            startActivity(it)
        }
    }

    /*override fun onBackPressed(): Boolean{
        mMainViewModel.mEbol = null
        return true
    }*/

}