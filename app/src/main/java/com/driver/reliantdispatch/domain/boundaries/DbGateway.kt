package com.driver.reliantdispatch.domain.boundaries

import androidx.paging.DataSource
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined


interface DbGateway{

    suspend fun getEbols(): MutableList<EbolJoined>

    fun getEbolsPaged(): DataSource.Factory<Int, EbolJoined>?

    suspend fun getCount(): Int?

    suspend fun saveEbol(ebolJoined: EbolJoined): Long

    suspend fun deleteEbol(ebolJoined: EbolJoined): Boolean
}