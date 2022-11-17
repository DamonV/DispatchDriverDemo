package com.driver.reliantdispatch.domain

import androidx.lifecycle.MutableLiveData
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined

class EbolsBackendUseCase {

    private val api: ApiGateway = App.component.getApiGateway()

    suspend fun getEbols(status: EbolStatus) = api.getEbols(status)

    fun getEbolsPaged(status: EbolStatus, addResult: MutableLiveData<ApiResponseDTO>) = api.getEbolsPaged(status, addResult)

    suspend fun postEbol(ebolJoined: EbolJoined) = when (ebolJoined.ebol?.status) {
        EbolStatus.DRAFTED.ordinal -> api.postEbol(ebolJoined)
        EbolStatus.ASSIGNED.ordinal -> {
            ebolJoined.ebol.status = EbolStatus.PICKED_UP.ordinal
            api.performInspection(ebolJoined)
        }
        EbolStatus.PICKED_UP.ordinal -> {
            ebolJoined.ebol.status = EbolStatus.DELIVERED.ordinal
            api.performInspection(ebolJoined)
        }
        else -> ApiResponseDTO(
            success = false,
            errorMsg = "System error"
        )
    }

    suspend fun saveEbol(ebolJoined: EbolJoined) = when (ebolJoined.ebol?.status) {
        EbolStatus.ASSIGNED.ordinal, EbolStatus.PICKED_UP.ordinal -> api.performInspection(ebolJoined)
        else -> ApiResponseDTO(
            success = false,
            errorMsg = "System error"
        )
    }

    suspend fun ebolPaid(id: Long?) = api.ebolPaid(id)

    suspend fun ebolCancel(id: Long) = api.ebolCancel(id)

    suspend fun ebolCancelRestore(id: Long) = api.ebolCancel(id, true)

    suspend fun ebolArchive(id: Long) = api.ebolArchive(id)

    suspend fun ebolArchiveRestore(id: Long) = api.ebolArchive(id, true)

    suspend fun ebolDelete(id: Long) = api.ebolDelete(id)

}