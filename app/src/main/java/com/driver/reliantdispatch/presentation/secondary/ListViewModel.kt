package com.driver.reliantdispatch.presentation.secondary

import android.util.Log
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.EbolsBackendUseCase
import com.driver.reliantdispatch.domain.LOG_TAG
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.dto.CountersDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined
import com.driver.reliantdispatch.domain.isNullOrEmpty
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent


abstract class ListViewModel(
    val status: EbolStatus
): ScopedViewModel() {
    private val global = App.component.getGlobal()

    protected val mEbolsBackendUseCase = EbolsBackendUseCase()

    val mEbolsList = ObservableField<PagedList<EbolJoined>>()

    val mEbolsListVisibility = ObservableBoolean(false)

    protected var mEbolsPagedListLiveData: LiveData<PagedList<EbolJoined>>? = null

    protected val mAdditionalResult = MutableLiveData<ApiResponseDTO>()

    private var isActive = false

    val mDataSourceFactory = mEbolsBackendUseCase.getEbolsPaged(status, mAdditionalResult)


    val mObserver = Observer<PagedList<EbolJoined>> {
        mEbolsList.set(it)
    }

    init {
        mAdditionalResult.observeForever { response ->
            if (!response.success && isActive)
                if (response.noInternet) viewModelEvent.value = PresentationEvent(EventType.NO_INTERNET)
                else if (response.notAuthorized) {
                    Log.d(LOG_TAG, "AssignedViewModel NAVIGATE nav_login")
                    viewModelEvent.value = PresentationEvent(EventType.NAVIGATE, R.id.nav_login)
                }
                else viewModelEvent.value = PresentationEvent(
                    EventType.SHOW_DIALOG,
                    -1,
                    R.string.dialog_title_error,
                    response.errorMsg
                )
            else if (response.success){
                mEbolsListVisibility.set(mEbolsList.get()?.size ?: 0 > 0)
            }
            mProgressVisibility.set(false)
        }
        retrieveData()
    }


    fun retrieveData(){
        if (mEbolsPagedListLiveData == null) {
            mProgressVisibility.set(true)
            mEbolsPagedListLiveData = mDataSourceFactory
            .toLiveData(
                //pageSize = 5
                Config(
                    pageSize = 5,
                    enablePlaceholders = true/*,
                    maxSize = 15*/
                )
            )
            mEbolsPagedListLiveData?.observeForever(mObserver)
        } else refresh()
    }

    fun refresh(){
        mEbolsPagedListLiveData?.value?.dataSource?.let{
            mProgressVisibility.set(true)
            it.invalidate()
        }
    }

    fun invalidateDataSource(changePosition: Int = -1){
        /*global.mCounters.observeForever(mCountersObserver)
        App.component.getEbolsCountersUseCase().restartRefreshCounters()*/
        mEbolsPagedListLiveData?.value?.dataSource?.invalidate()
    }

    /*val mCountersObserver = object : Observer<CountersDTO?> {
        var c = 0

        override fun onChanged(t: CountersDTO?) {
            if (c++ == 1) {
                mEbolsPagedListLiveData?.value?.dataSource?.invalidate()
                global.mCounters.removeObserver(this)
                c = 0
            }
        }
    }*/

    open fun onResume(){
        isActive = true
        if (mEbolsList.get()?.size.isNullOrEmpty() || global.listNeedRefreshByStatus == status) retrieveData()
        //if (global.listNeedRefreshByStatus == status)
        global.listNeedRefreshByStatus = null
    }

    open fun onPause(){
        isActive = false
    }

    open fun onRefresh(){
        refresh()
    }
}