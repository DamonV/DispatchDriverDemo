package com.driver.reliantdispatch.domain

import androidx.lifecycle.MutableLiveData
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.dto.CountersDTO
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val REFRESH_INTERVAL = 60000L

class EbolsCountersUseCase: ScopedUseCase() {
    private val api: ApiGateway = App.component.getApiGateway()
    private val db = App.component.getDbGateway()

    var mTimerJob: Job? = null
    var mResult: MutableLiveData<ApiResponseDTO>? = null


    fun startRefreshCounters(result: MutableLiveData<ApiResponseDTO>?) {
        var needLaunch = mResult == null || result == null
        if (mResult != null && result != mResult) {
            mTimerJob?.cancel()
            needLaunch = true
        }
        result?.let { mResult = it }

        if (needLaunch) mTimerJob = ioScope.launch {
            while (true) {
                val apiJob = async { api.getCounters() }
                val drafted = db.getCount()
                val response = apiJob.await()
                drafted?.let{
                    if (response.success && response.body != null)
                        (response.body as CountersDTO).drafted = it
                }
                mResult?.postValue(response)
                delay(REFRESH_INTERVAL)
            }
        }
    }

    fun stopRefreshCounters(result: MutableLiveData<ApiResponseDTO>? = null){
        if (result != null && result == mResult) {
            mTimerJob?.cancel()
            mResult = null
        }// else if (result == null) mTimerJob?.cancel()
    }

    fun restartRefreshCounters(){
        //stopRefreshCounters(null)
        startRefreshCounters(null)
    }

}