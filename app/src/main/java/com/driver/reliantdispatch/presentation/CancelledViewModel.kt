package com.driver.reliantdispatch.presentation

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.dto.EbolShortestDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ListViewModel
import kotlinx.coroutines.launch


class CancelledViewModel: ListViewModel(EbolStatus.CANCELED) {

    fun onDeleteClick(item: EbolJoined, pos: Int){
        item.ebol?.id?.let {
            uiScope.launch {
                val response = mEbolsBackendUseCase.ebolDelete(it)
                if (response.success) {
                    invalidateDataSource(pos)
                } else if (response.noInternet) viewModelEvent.value =
                    PresentationEvent(EventType.NO_INTERNET)
                else if (response.notAuthorized) viewModelEvent.value =
                    PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                else viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    response.errorMsg
                )
            }
        }
    }

    fun onRestoreClick(item: EbolJoined, pos: Int){
        item.ebol?.let {
            uiScope.launch {
                val response = mEbolsBackendUseCase.ebolCancelRestore(it.id)
                if (response.success) {
                    invalidateDataSource(pos)
                    val loadId = it.loadId ?: "eBOL"
                    val restoredStatus = (response.body as EbolShortestDTO).statusE?.text ?: "<>"
                    viewModelEvent.value = PresentationEvent(
                        EventType.SHOW_DIALOG,
                        -1,
                        R.string.dialog_title_info,
                        context.resources.getString(R.string.text_restore_ebol, loadId, restoredStatus)
                    )
                    App.component.getGlobal().listNeedRefreshByStatus = response.body.statusE
                } else if (response.noInternet) viewModelEvent.value =
                    PresentationEvent(EventType.NO_INTERNET)
                else if (response.notAuthorized) viewModelEvent.value =
                    PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                else viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    response.errorMsg
                )
            }
        }
    }

}