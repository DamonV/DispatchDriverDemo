package com.driver.reliantdispatch

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.driver.reliantdispatch.databinding.ActivityMainBinding
import com.driver.reliantdispatch.databinding.NavHeaderMainBinding
import com.driver.reliantdispatch.domain.AuthUseCase
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.presentation.secondary.BaseFragment
import com.driver.reliantdispatch.presentation.MainViewModel
import com.driver.reliantdispatch.presentation.REQ_CHECK_SETTINGS
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mNavController: NavController
    lateinit var mViewModel: MainViewModel
    private val global = App.component.getGlobal()
    private var prevMenuItemId = R.id.nav_ebols
    //private var mSavedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = mViewModel

        val bindingNavHeader: NavHeaderMainBinding = DataBindingUtil.inflate(layoutInflater, R.layout.nav_header_main, nav_view, false)
        bindingNavHeader.viewModel = mViewModel
        nav_view.addHeaderView(bindingNavHeader.root)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)

        mNavController = findNavController(R.id.nav_host_fragment_main)
        mNavController.addOnDestinationChangedListener { _, destination, args ->
            prevMenuItemId = destination.id
            nav_view.menu.findItem(destination.id)?.isChecked = true
            when (destination.id) {
                R.id.nav_login,
                R.id.nav_vin_scanner,
                R.id.nav_no_internet,
                R.id.nav_condition,
                R.id.nav_damage_marking
                -> {
                    toolbar.visibility = View.GONE
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                }
                R.id.nav_forgot -> {
                    toolbar.visibility = View.VISIBLE
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)

                }
                else -> {
                    toolbar.visibility = View.VISIBLE
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                }
            }
            if (destination.id != R.id.nav_vin_scanner
                && destination.id != R.id.nav_condition
                && destination.id != R.id.nav_damage_marking
                && requestedOrientation != SCREEN_ORIENTATION_UNSPECIFIED)
                requestedOrientation = SCREEN_ORIENTATION_UNSPECIFIED
            mViewModel.mProgressVisibility.set(false)
        }
        //toolbar.setupWithNavController(mNavController, drawer_layout) //setting up NavigationUI with toolbar, drawer and graph (kotlin extension)

        setupActionBarWithNavController(mNavController, drawer_layout)
        //NavigationUI.setupWithNavController(nav_view, mNavController)

        nav_view.setNavigationItemSelectedListener(this)

        if (global.mRememberMe == false && savedInstanceState == null){
            global.mToken = null
            global.mExpiresAt = null
            global.mUser = null
        }
        //mSavedInstanceState = savedInstanceState

        global.mLocationPermissionGranted.observe(this, Observer { granted ->
            Log.d(LOG_TAG, "mLocationPermissionGranted $granted")
            if (granted) App.component.getGPSServiceUseCase().startReliantService()
        })
    }

    private fun checkSession(){
        val needCheck = when (mNavController.currentDestination?.id) {
            R.id.nav_login,
            R.id.nav_forgot,
            R.id.nav_no_internet -> false
            else -> true}
        if (needCheck)
            if (global.isTokenExpired() || global.mToken.isNullOrEmpty())
                if (global.mRememberMe == true && !global.mToken.isNullOrEmpty()) {
                    GlobalScope.launch {
                        val response = AuthUseCase().refresh()
                        withContext (Dispatchers.Main) {
                            if (response.noInternet) mNavController.navigate(R.id.nav_no_internet)
                            else if (!response.success) mNavController.navigate(R.id.nav_login)
                        }
                    }
                } else mNavController.navigate(R.id.nav_login)
            else GlobalScope.launch {
                AuthUseCase().obtainProfile()
            }
    }

    override fun onStart() {
        super.onStart()
        checkSession()
    }

    private fun handleBackPress(): Boolean{         //true - if handle in the fragment
        //propagate back-click to fragment level
        when (mNavController.currentDestination?.id) {
            R.id.nav_ebols,
            R.id.nav_no_internet -> {}
            else -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main)
                try {
                    return (navHostFragment?.childFragmentManager?.primaryNavigationFragment as BaseFragment).onBackPressed()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        return false
    }

    override fun onBackPressed() {
        if (handleBackPress()) return

        //other special conditions
        when (mNavController.currentDestination?.id) {
            R.id.nav_no_internet -> mViewModel.isNoInternet = false
            R.id.nav_login -> return
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return handleBackPress() || NavigationUI.navigateUp(mNavController, drawer_layout)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(LOG_TAG,"wow ${item.itemId}")
        return super.onOptionsItemSelected(item)
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        drawer_layout.closeDrawer(GravityCompat.START)
        //Log.d(LOG_TAG, "${item.title} ${item.itemId} ?= ${prevMenuItemId}")
        if (item.itemId == prevMenuItemId) return true
        when (item.itemId) {
            R.id.nav_contact -> {
                /*val sendIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.nav_text_contact_us_email)))
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.nav_text_contact_us_subject))
                }
                startActivity(sendIntent)*/

                return true
            }
            R.id.nav_logout -> {
                if (mNavController.currentDestination?.id == R.id.nav_ebols) mNavController.navigate(R.id.nav_login)
                else {
                    mViewModel.mNavDestination = R.id.nav_login
                    mNavController.popBackStack(R.id.nav_ebols, false)
                }

                GlobalScope.launch {
                    val response = mViewModel.logout()
                    if (response.noInternet) mNavController.navigate(R.id.nav_no_internet)
                }
                return true
            }
        }
        //prevMenuItemId = item.itemId
        return NavigationUI.onNavDestinationSelected(item, mNavController)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CHECK_SETTINGS)
            global.mLocationSettingsRequest.value = resultCode == Activity.RESULT_OK
        Log.d(LOG_TAG, "onActivityResult $resultCode")
    }
}
