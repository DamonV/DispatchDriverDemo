package com.driver.reliantdispatch.data

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined

class EbolsDataSourceFactory(
    val mAdditionalResult: MutableLiveData<ApiResponseDTO>,
    val mStatus: EbolStatus
): DataSource.Factory<Int, EbolJoined>(){
    override fun create(): DataSource<Int, EbolJoined>{
        return EbolsPageKeyedDataSource(mAdditionalResult, mStatus)
    }
}