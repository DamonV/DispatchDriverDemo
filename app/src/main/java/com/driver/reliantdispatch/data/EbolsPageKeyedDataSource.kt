package com.driver.reliantdispatch.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.dto.CountersDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.isNullOrEmpty
import kotlinx.coroutines.*
import java.util.*


class EbolsPageKeyedDataSource(
    val mAdditionalResult: MutableLiveData<ApiResponseDTO>,
    val mStatus: EbolStatus
): PageKeyedDataSource<Int, EbolJoined>(){
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val api: ApiGateway = App.component.getApiGateway()
    //private val global = App.component.getGlobal()

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, EbolJoined>
    ) {
        ioScope.launch {
            val apiJob = async { api.getCounters() }
            val response = api.getEbols(mStatus, 1, params.requestedLoadSize)
            val responseCounters = apiJob.await()
            if (response.success) {
                val list = response.body as? List<EbolJoined>
                val totalCount = if (responseCounters.success) (responseCounters.body as CountersDTO).getCounterByStatus(mStatus)
                                else null
                //global.mCounters.value?.getCounterByStatus(mStatus)
                //Log.d(LOG_TAG, "totalCount $mStatus $totalCount")
                if (list != null) {
                    val nextKey = if (list.size < params.requestedLoadSize) null
                                    else 4
                    if (!totalCount.isNullOrEmpty() && params.placeholdersEnabled && list.isNotEmpty())
                        try {
                            callback.onResult(list, 0, totalCount ?: 0, null, nextKey)
                        } catch (e: IllegalArgumentException){
                            callback.onResult(list, null, nextKey)
                        }
                    else
                        callback.onResult(list, null, nextKey)
                } else
                    callback.onResult(listOf<EbolJoined>(), null, null)
            }
            mAdditionalResult.postValue(response)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, EbolJoined>) {
        ioScope.launch {
            val response = api.getEbols(mStatus, params.key, params.requestedLoadSize)
            if (response.success) {
                val list = response.body as? List<EbolJoined>
                if (list != null) {
                    val nextKey = if (list.size < params.requestedLoadSize) null
                                    else params.key + 1
                    callback.onResult(list, nextKey)
                } else
                    callback.onResult(listOf<EbolJoined>(), null)
            }
            mAdditionalResult.postValue(response)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, EbolJoined>) {
        ioScope.launch {
            val response = api.getEbols(mStatus, params.key, params.requestedLoadSize)
            if (response.success) {
                val list = response.body as? List<EbolJoined>
                val prevKey = if (params.key <= 1) null
                                else params.key - 1
                if (list != null)
                    callback.onResult(list, prevKey)
                else
                    callback.onResult(listOf<EbolJoined>(), prevKey)
            }
            mAdditionalResult.postValue(response)
        }
    }
}