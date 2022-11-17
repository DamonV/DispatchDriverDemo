package com.driver.reliantdispatch.presentation

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.dto.CountersDTO
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel


class EbolsViewModel: ScopedViewModel() {
    var mBadges = ObservableField<Array<Int>>(arrayOf(0, 0, 0, 0, 0))
    var mBadgesVisibility = ObservableField<Array<Boolean>>(arrayOf(true, true, true, true, false))
    val mEbolsCountersUseCase = App.component.getEbolsCountersUseCase()
    val mResult = MutableLiveData<ApiResponseDTO>()

    init {
        mResult.observeForever { response ->
            response?.let{ response ->
                if (response.success){
                    val counters = response.body as CountersDTO
                    mBadges.set(arrayOf(
                        counters.drafted ?: 0,
                        counters.assigned ?: 0,
                        counters.picked_up ?: 0,
                        counters.delivered ?: 0,
                        0//counters.paid ?: 0
                    ))
                }
            }
        }
    }

    /*fun onNavDestinationChanged(destId: Int){
        mBadgesVisibility.set(
            when(destId){
                R.id.nav_bottom_drafted -> arrayOf(false, true, true, true, false)
                R.id.nav_bottom_assigned -> arrayOf(true, false, true, true, false)
                R.id.nav_bottom_picked_up -> arrayOf(true, true, false, true, false)
                R.id.nav_bottom_delivered -> arrayOf(true, true, true, false, false)
                else -> arrayOf(true, true, true, true, false)
            })
    }*/

    fun onStart(){
        mEbolsCountersUseCase.startRefreshCounters(mResult)
    }

    fun onStop(){
        mEbolsCountersUseCase.stopRefreshCounters(mResult)
    }

    fun restartRefreshCounters(){
        mEbolsCountersUseCase.restartRefreshCounters()
    }

}