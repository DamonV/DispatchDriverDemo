package com.driver.reliantdispatch.presentation.secondary

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.presentation.dto.PresentationEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import javax.inject.Inject

open class ScopedViewModel: ViewModel() {
    private val job = SupervisorJob()
    protected val uiScope = CoroutineScope(Dispatchers.Main + job)
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)

    val viewModelEvent = SingleLiveEvent<PresentationEvent>()
    val mProgressVisibility = ObservableBoolean()

    @Inject
    lateinit var context: Context

    init {
        App.component.inject(this)
    }

    override fun onCleared() {
        super.onCleared()
        uiScope.coroutineContext.cancelChildren()
        ioScope.coroutineContext.cancelChildren()
    }
}