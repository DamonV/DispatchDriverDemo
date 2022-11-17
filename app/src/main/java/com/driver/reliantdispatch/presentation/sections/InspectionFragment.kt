package com.driver.reliantdispatch.presentation.sections

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentInspectionBinding
import com.driver.reliantdispatch.domain.*
import com.driver.reliantdispatch.domain.entities.Attachment
import com.driver.reliantdispatch.domain.entities.joined.InspectImageJoined
import kotlinx.android.synthetic.main.fragment_inspection.*
import kotlinx.android.synthetic.main.fragment_inspection.parentLayout


class InspectionFragment: BaseSectionFragment() {
    var mInspectType: Int = -1
    var readOnly: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mViewModel = ViewModelProviders.of(this).get(InspectionViewModel::class.java)

        mBinding = FragmentInspectionBinding.inflate(layoutInflater, container, false)
        with (mBinding as FragmentInspectionBinding) {
            lifecycleOwner = this@InspectionFragment
            fragment = this@InspectionFragment
            viewModel = mViewModel as InspectionViewModel
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        (mViewModel as InspectionViewModel).mVehicleIndex = arguments?.getInt(ARG_1, -1) ?: -1
        mInspectType = arguments?.getInt(ARG_2, -1) ?: -1
        (mViewModel as InspectionViewModel).mInspectionType = mInspectType
        readOnly = arguments?.getBoolean(ARG_READ_ONLY, false) ?: false

        (mViewModel as InspectionViewModel).onActivityCreated(activity)

        super.onActivityCreated(savedInstanceState)

        /*if (savedInstanceState == null && arguments?.getBoolean(ARG_DETAILS, false) == true)
            activity?.let{
                val detailsViewModel = ViewModelProviders.of(it).get(DetailsViewModel::class.java)
                (mViewModel as SectionViewModel).mItem.set(detailsViewModel.mItem.get()?.copy())
            }*/

        (activity as AppCompatActivity).supportActionBar?.title = when(mInspectType) {
            DELIVERY_INSPECTION -> getString(R.string.title_delivery_inspection)
            else -> getString(R.string.title_pickup_inspection)
        }

        if (mMainViewModel.mImagesList!=null){   // comeback from condition fragment
            (mViewModel as InspectionViewModel).mImagesList.set(mMainViewModel.mImagesList)
            mMainViewModel.mImagesList = null
        } else if (mMainViewModel.mAdditionalInspection != null){   // comeback from additional inspection fragment
            (mViewModel as InspectionViewModel).mAdditionalInspection.set(mMainViewModel.mAdditionalInspection)
            mMainViewModel.mAdditionalInspection = null
        }

        nestedListView.adapter = InspectImagesAdapter(this, mInspectType)
        nestedListView2.adapter = AttachmentsAdapter(this)

        if (readOnly)
            for (i in 0 until parentLayout.childCount) {
                val child = parentLayout.getChildAt(i)
                if (child.id == R.id.buttonLayout) continue
                child.isEnabled = false
                child.isClickable = false
                if (child is ViewGroup) for (j in 0 until child.childCount) {
                    val subchild = child.getChildAt(j)
                    subchild.isEnabled = false
                    subchild.isClickable = false
                    if (subchild is ViewGroup) for (k in 0 until subchild.childCount) {
                        val subsubchild = subchild.getChildAt(k)
                        subsubchild.isEnabled = false
                        subsubchild.isClickable = false
                    }
                }
            }
        //else Global.checkLocationSettings(activity)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.action_save)?.isVisible = !readOnly
    }

    /*override fun onResume() {
        super.onResume()
        (mViewModel as InspectionViewModel).onResume(readOnly, activity)
    }*/

    override fun onStart() {
        super.onStart()
        if (mMainViewModel.mPhotosPathArr != null) {
            val list = mMainViewModel.mPhotosPathArr!!
            onPhotosChoice(MutableList(list.size) { list[it] })
            mMainViewModel.mPhotosPathArr = null
        }
        else if (mMainViewModel.mFilesPathArr != null) {
            val list = mMainViewModel.mFilesPathArr!!
            onFilesChoice(MutableList(list.size) { list[it] })
            mMainViewModel.mFilesPathArr = null
        }
    }

    override fun onStop() {
        super.onStop()
        (mViewModel as InspectionViewModel).onStop()
    }

    override fun onTryConnectionAgain(){
        (mViewModel as InspectionViewModel).onTryConnectionAgain()
    }

    fun onClickPerformInspection(v: View, pos: Int){
        if (readOnly) return
        mMainViewModel.mImagesList = (mViewModel as InspectionViewModel).mImagesList.get()
        val args = Bundle()
        args.putInt(ARG_1, mInspectType)
        args.putInt(ARG_2, pos)
        Navigation.findNavController(v).navigate(R.id.nav_condition, args)
    }

    fun onAddInspClick(v: View){
        (mViewModel as InspectionViewModel).mAdditionalInspection.get()?.let{
            mMainViewModel.mAdditionalInspection = it.copy()
        }
        Navigation.findNavController(v).navigate(R.id.nav_additional_inspection)
    }

    @SuppressLint("RestrictedApi")
    fun onAddAttachmentsClick(v: View){
        activity?.let {
            AddAttachmentsBottomDialogFragment.newInstance().show(it.supportFragmentManager, "")
        }
    }

    private fun onPhotosChoice(pathArr: MutableList<String>) {
        (mViewModel as InspectionViewModel).onPhotosChoice(pathArr, activity)
    }

    private fun onFilesChoice(pathArr: MutableList<String>){
        (mViewModel as InspectionViewModel).onFilesChoice(pathArr)
    }

    fun onClickRemoveImage(v: View, pos: Int = -1){
        if (readOnly) return
        val builder = AlertDialog.Builder(v.context)
        builder.setTitle(R.string.dialog_title_warning)
            .setMessage(R.string.remove_image_confirm)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()

                with((mViewModel as InspectionViewModel)) {
                    mImagesList.set(mImagesList.get()?.let {
                        if (pos in 0 until it.size) it.removeAt(pos)
                        val newList = mutableListOf<InspectImageJoined>()
                        newList.addAll(it)
                        newList
                    })
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
            .create().show()
    }

    fun onClickRemoveAttachment(v: View, pos: Int = -1){
        if (readOnly) return
        val builder = AlertDialog.Builder(v.context)
        builder.setTitle(R.string.dialog_title_warning)
            .setMessage(R.string.remove_file_confirm)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog, _ ->
                dialog.cancel()

                with((mViewModel as InspectionViewModel)) {
                    mAttachmentList.set(mAttachmentList.get()?.let {
                        val newList = mutableListOf<Attachment>()
                        newList.addAll(it)
                        if (pos in 0 until newList.size) newList.removeAt(pos)
                        newList
                    })
                }
            }
            .setNegativeButton(
                android.R.string.cancel
            ) { dialog, _ -> dialog.cancel() }
            .create().show()
    }
}