package com.driver.reliantdispatch.presentation

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.driver.reliantdispatch.*
import com.driver.reliantdispatch.databinding.ViewBadgeBinding
import com.driver.reliantdispatch.domain.*
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_ebols.*
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView

const val REQ_CODE: Int = 101
const val MY_PERMISSIONS_REQ_CODE = 102
const val MY_PERMISSIONS_CAMERA_REQ_CODE = 103
const val REQ_CHECK_SETTINGS: Int = 203

class EbolsFragment: Fragment() {
    private lateinit var mNavControllerBottom: NavController
    lateinit var mViewModel: EbolsViewModel
    private val global = App.component.getGlobal()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        mViewModel = ViewModelProviders.of(this).get(EbolsViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_ebols, container, false)

        val bottomNav: BottomNavigationView = root.findViewById(R.id.bottom_nav)
        val bottomNavigationMenuView = bottomNav.getChildAt(0) as BottomNavigationMenuView
        for (i in 0 until bottomNavigationMenuView.childCount) {
            val itemView = bottomNavigationMenuView.getChildAt(i) as BottomNavigationItemView

            val binding = ViewBadgeBinding.inflate(inflater, itemView, true)
            binding.viewModel = mViewModel
            binding.index = i
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.also{ activity ->
            mNavControllerBottom = Navigation.findNavController(activity, R.id.nav_host_fragment_bottom)
            bottom_nav.setupWithNavController(mNavControllerBottom)

            /*mNavControllerBottom.setGraph(R.navigation.nav_graph_bottom)
            NavigationUI.setupWithNavController(bottom_nav, mNavControllerBottom)*/

            mNavControllerBottom.addOnDestinationChangedListener { _, destination, _ ->
                if (destination.id == R.id.nav_bottom_paid) mViewModel.restartRefreshCounters()
                //mViewModel.onNavDestinationChanged(destination.id)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (global.mLocationPermissionGranted.value != true) {   /*!global.isChecked || */
            //Log.d(LOG_TAG, "let check permissions")
            checkGooglePlayServicesAvailable()
            checkPermissions()
            activity?.let { Global.checkLocationSettings(it) }
            //global.isChecked = true
        } //else startReliantService()

        mViewModel.onStart()

        activity?.also { activity ->
            val mainViewModel = ViewModelProviders.of(activity).get(MainViewModel::class.java)

            if (mainViewModel.mNavBottomDestination != null) {
                bottom_nav.selectedItemId = mainViewModel.mNavBottomDestination!!
                mainViewModel.mNavBottomDestination = null
                //mNavControllerBottom.navigate(mainViewModel.mNavBottomDestination!!)

            } else if (mainViewModel.mNavDestination != null) {
                Navigation.findNavController(activity, R.id.nav_host_fragment_main).navigate(mainViewModel.mNavDestination!!)
                mainViewModel.mNavDestination = null
            }
        }

        Log.d(LOG_TAG, "isCleanLogin = ${global.isCleanLogin}")
        if (global.isCleanLogin) {
            global.isCleanLogin = false
            if (mNavControllerBottom.currentDestination?.id != R.id.nav_bottom_assigned) bottom_nav.selectedItemId = R.id.nav_bottom_assigned
        }
    }

    override fun onStop() {
        super.onStop()
        mViewModel.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ebols, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new -> {
                Navigation.findNavController(activity as FragmentActivity, R.id.nav_host_fragment_main)
                    .navigate(R.id.nav_create,
                        Bundle().let{
                        it.putBoolean(ARG_2, true)
                        it
                    })
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun checkGooglePlayServicesAvailable(){
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val code = googleApiAvailability.isGooglePlayServicesAvailable(context)
        if (code != ConnectionResult.SUCCESS) {
            googleApiAvailability.getErrorDialog(activity, code, REQ_CODE).show()
        }
    }

    private fun checkPermissions(){
        val permission = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            global.mLocationPermissionGranted.value = true
        } else {
            permission.add(Manifest.permission.ACCESS_COARSE_LOCATION)
            permission.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (ContextCompat.checkSelfPermission(context!!,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            permission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(context!!,
                Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            permission.add(Manifest.permission.CALL_PHONE)
        }

        if (permission.isNotEmpty()) requestPermissions(
            permission.toTypedArray(),
            MY_PERMISSIONS_REQ_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQ_CODE -> {
                if (grantResults.isNotEmpty()
                    && permissions.all{it == Manifest.permission.ACCESS_COARSE_LOCATION
                            || it == Manifest.permission.ACCESS_FINE_LOCATION}
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {      //if answer for location permissions
                    global.mLocationPermissionGranted.value = true
                }
                return
            }
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if (requestCode == REQ_CHECK_SETTINGS && resultCode == Activity.RESULT_OK){
            mViewModel.onStart()
        }
    }*/
}