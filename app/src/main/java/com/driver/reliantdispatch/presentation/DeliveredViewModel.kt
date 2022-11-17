package com.driver.reliantdispatch.presentation

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ListViewModel
import kotlinx.coroutines.launch


class DeliveredViewModel: ListViewModel(EbolStatus.DELIVERED) {

    fun onPaidClick(id: Long?){
        uiScope.launch {
            val response = mEbolsBackendUseCase.ebolPaid(id)
            if (response.success){
                viewModelEvent.value = PresentationEvent(EventType.NAVIGATE_BOTTOM, R.id.nav_bottom_paid)
            }
            else if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
            else if (response.notAuthorized) viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
            else viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                response.errorMsg
            )
        }
    }

    fun onCancelClick(item: EbolJoined, pos: Int) {
        item.ebol?.id?.let {
            uiScope.launch {
                val response = mEbolsBackendUseCase.ebolCancel(it)
                if (response.success) {
                    invalidateDataSource()
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