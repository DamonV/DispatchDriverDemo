package com.driver.reliantdispatch.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentCreateBinding
import com.driver.reliantdispatch.domain.ARG_1
import com.driver.reliantdispatch.domain.ARG_2
import com.driver.reliantdispatch.domain.entities.Company
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.secondary.CompanyActionFragment
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.android.synthetic.main.fragment_create.*

enum class EbolFragmentType {
    CREATE, SUBMIT_PICKUP, SUBMIT_DELIVERY
}

class CreateFragment: BaseFragment(), CompanyActionFragment {
    lateinit var mBinding: FragmentCreateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        mBinding = FragmentCreateBinding.inflate(layoutInflater, container, false)
        mBinding.lifecycleOwner = this
        mBinding.fragment = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        var fragType = EbolFragmentType.values()[arguments?.getInt(ARG_1, 0) ?: 0]
        val initViewModelNeeding = arguments?.getBoolean(ARG_2) ?: false
        arguments?.remove(ARG_2)

        activity?.let{
            mViewModel = ViewModelProviders.of(it).get(CreateViewModel::class.java)
            mBinding.viewModel = mViewModel as CreateViewModel

            with (mViewModel as CreateViewModel) {
                if (savedInstanceState != null && mItem.get() == null) {
                    val savedJson = savedInstanceState.getString(ARG_1, "")
                    try {
                        mItem.set(Gson().fromJson(savedJson, EbolJoined::class.java))
                    } catch (e: JsonSyntaxException) {
                        e.printStackTrace()
                    }
                    fragType = EbolFragmentType.values()[savedInstanceState.getInt(ARG_2, 0)]
                } else if (fragType == EbolFragmentType.CREATE && initViewModelNeeding) initNewEbol()
                mFragmentType.set(fragType)
                onStart()
            }
        }

        super.onActivityCreated(savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = when(fragType) {
            EbolFragmentType.SUBMIT_PICKUP -> getString(R.string.title_submit_pickup)
            EbolFragmentType.SUBMIT_DELIVERY -> getString(R.string.title_submit_delivery)
            else -> getString(R.string.title_create)
        }

        nestedListView.adapter = VehiclesAdapter(mViewModel as CreateViewModel)

        val progressDrawable = progressBar.indeterminateDrawable.mutate()
        progressDrawable.setColorFilter(resources.getColor(R.color.slate), android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.indeterminateDrawable = progressDrawable

        nestedScrollView.scrollTo(0, (mViewModel as CreateViewModel).mScrollPosition)
    }

    override fun onStop() {
        super.onStop()
        (mViewModel as CreateViewModel).mScrollPosition = nestedScrollView.scrollY
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with (mViewModel as CreateViewModel) {
            mItem.get()?.let {
                outState.putString(ARG_1, Gson().toJson(it))
            }
            mFragmentType.get()?.let {
                outState.putInt(ARG_2, it.ordinal)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        //if ((mViewModel as CreateViewModel).mItem.get()?.ebol?.status == EbolStatus.DRAFTED.ordinal)
            inflater.inflate(R.menu.save, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_save -> {
                (mViewModel as CreateViewModel).onSaveClick()
                //Navigation.findNavController(activity as FragmentActivity, R.id.nav_host_fragment_main).navigateUp()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
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

    fun onCancelClick(){
        val builder = AlertDialog.Builder(context!!)
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
        dialog.show()
    }

    fun onClickSubmitPickupDelivery(v: View){
        if (!(mViewModel as CreateViewModel).checkEbol()) return

        val builder = AlertDialog.Builder(v.context, R.style.AlertDialogTheme)
        builder.setTitle(R.string.dialog_title_warning)
            .setMessage(v.resources.getString(
                if ((mViewModel as CreateViewModel).mFragmentType.get() == EbolFragmentType.SUBMIT_DELIVERY) R.string.text_submit_delivery
                else R.string.text_submit_pickup
            ))
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()
                (mViewModel as CreateViewModel).onClickSubmitPickupDelivery()
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
            .create().show()
    }


    override fun onTryConnectionAgain(){
        (mViewModel as CreateViewModel).perfomSubmitPickupDelivery()
    }

    override fun onRepeatAction(){
        (mViewModel as CreateViewModel).perfomSubmitPickupDelivery()
    }

    override fun onCancelRepeatAction(){
        activity?.let {
            Navigation.findNavController(it, R.id.nav_host_fragment_main)
                .navigateUp()
        }
    }

}