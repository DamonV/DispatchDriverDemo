package com.driver.reliantdispatch.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren

abstract class ScopedUseCase {
    private val job = SupervisorJob()
    protected val ioScope = CoroutineScope(Dispatchers.IO + job)

    fun onStop() {
        ioScope.coroutineContext.cancelChildren()
    }
}