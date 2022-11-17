package com.driver.reliantdispatch.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentEbolsListBinding
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.secondary.ListViewModel
import kotlinx.android.synthetic.main.fragment_ebols_list.*

class PickedupFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        mViewModel = ViewModelProviders.of(this).get(PickedupViewModel::class.java)

        val binding = FragmentEbolsListBinding.inflate(layoutInflater, container, false)
        binding.viewModel = mViewModel as PickedupViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_picked_up)

        val adapter = PickedupEbolsAdapter(mViewModel as PickedupViewModel, activity as AppCompatActivity)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        //(recyclerView.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false
        //recyclerView.getItemAnimator()?.setChangeDuration(0)
        recyclerView.itemAnimator = null
    }

    override fun onResume() {
        super.onResume()
        (mViewModel as ListViewModel).onResume()
    }

    override fun onPause() {
        super.onPause()
        (mViewModel as ListViewModel).onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.refresh, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_refresh -> {
                (mViewModel as PickedupViewModel).onRefresh()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}