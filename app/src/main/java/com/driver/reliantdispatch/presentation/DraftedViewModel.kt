package com.driver.reliantdispatch.presentation

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.ARG_2
import com.driver.reliantdispatch.domain.CreateEbolUseCase
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.isNullOrEmpty
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import com.driver.reliantdispatch.presentation.secondary.ScopedViewModel
import kotlinx.coroutines.launch


class DraftedViewModel: ScopedViewModel() {

    //private val global = App.component.getGlobal()

    val mEbolsList = ObservableField<PagedList<EbolJoined>>()

    var mEbolsPagedListLiveData: LiveData<PagedList<EbolJoined>>? = null

    init {
        mProgressVisibility.set(true)
        mEbolsPagedListLiveData = CreateEbolUseCase().getEbolsPaged()?.toLiveData(
            Config(
                pageSize = 5,
                enablePlaceholders = true,
                maxSize = 15
            )
        )
        mEbolsPagedListLiveData?.observeForever {
            val s = it.dataSource
            mEbolsList.set(it)
            mProgressVisibility.set(false)
        }
    }


    /*fun onStart(){
        /*uiScope.launch {
            mProgressVisibility.set(true)
            mEbolsList.set(CreateEbolUseCase().getEbols())
            mProgressVisibility.set(false)
        }*/
    }*/

    /*fun onResume(){
        if (mEbolsList.get()?.size.isNullOrEmpty() || global.listNeedRefreshByStatus == EbolStatus.DRAFTED) {
            mEbolsPagedListLiveData?.value?.dataSource?.let{
                mProgressVisibility.set(true)
                it.invalidate()
            }
        }
        global.listNeedRefreshByStatus = null
    }*/

    fun onClickAdd(v: View) {
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            R.id.nav_create,
            -1,
            null,
            null,
            Bundle().let{
                it.putBoolean(ARG_2, true)
                it
            }
        )
    }

    fun onDeleteClick(item: EbolJoined, pos: Int){
        uiScope.launch {
            if (CreateEbolUseCase().deleteEbol(item)) {
                //mEbolsList.get()?.let{ list ->
                    //list.removeAt(pos)
                    //mEbolsList.set(PagedList(list.size) { list[it].copy()})
                //}
                App.component.getEbolsCountersUseCase().restartRefreshCounters()
            }
            else viewModelEvent.value = PresentationEvent(
                EventType.SHOW_DIALOG,
                -1,
                R.string.dialog_title_error,
                context.resources.getString(R.string.error_db_writing)
            )
        }
    }

}