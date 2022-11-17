package com.driver.reliantdispatch.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PositionalDataSource
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import kotlinx.coroutines.*


class EbolsPositionalDataSource(
    val mAdditionalResult: MutableLiveData<ApiResponseDTO>,
    val mStatus: EbolStatus
): PositionalDataSource<EbolJoined>(){
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val api: ApiGateway = App.component.getApiGateway()

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<EbolJoined>) {
        ioScope.launch {
            val page = (params.startPosition / params.loadSize) + 1
            Log.d(LOG_TAG, "startPosition=${params.startPosition} loadSize=${params.loadSize}")
            Log.d(LOG_TAG, "page=$page")
            val response = api.getEbols(mStatus, page, params.loadSize)
            if (response.success) {
                val list = response.body as? List<EbolJoined>
                if (list != null) callback.onResult(list)
                else callback.onResult(listOf<EbolJoined>())
            }
            mAdditionalResult.postValue(response)
        }
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<EbolJoined>) {
        ioScope.launch {
            val page = (params.requestedStartPosition / params.requestedLoadSize) + 1
            val position = (page - 1) * params.requestedLoadSize
            Log.d(LOG_TAG, "requestedStartPosition=${params.requestedStartPosition} requestedLoadSize=${params.requestedLoadSize} pageSize=${params.pageSize}")
            Log.d(LOG_TAG, "page=$page position=$position")
            val response = api.getEbols(mStatus, page, params.requestedLoadSize)
            if (response.success) {
                val list = response.body as? List<EbolJoined>
                if (list != null && list.isNotEmpty()) callback.onResult(list, position)
                //else callback.onResult(listOf<EbolJoined>(), position)
            }
            mAdditionalResult.postValue(response)
        }
    }

}