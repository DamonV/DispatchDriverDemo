package com.driver.reliantdispatch.presentation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.databinding.FragmentEbolsListBinding
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.secondary.ListViewModel
import kotlinx.android.synthetic.main.fragment_drafted.*

class DeliveredFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        mViewModel = ViewModelProviders.of(this).get(DeliveredViewModel::class.java)

        val binding = FragmentEbolsListBinding.inflate(layoutInflater, container, false)
        binding.viewModel = mViewModel as DeliveredViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.title_delivered)

        val adapter = DeliveredEbolsAdapter(mViewModel as DeliveredViewModel, activity as AppCompatActivity)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
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
                (mViewModel as DeliveredViewModel).onRefresh()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}