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

class ArchivedFragment: BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)

        mViewModel = ViewModelProviders.of(this).get(ArchivedViewModel::class.java)

        val binding = FragmentEbolsListBinding.inflate(layoutInflater, container, false)
        binding.viewModel = mViewModel as ArchivedViewModel
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val adapter = ArchivedEbolsAdapter(mViewModel as ArchivedViewModel, activity as AppCompatActivity)
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
                (mViewModel as ArchivedViewModel).onRefresh()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}