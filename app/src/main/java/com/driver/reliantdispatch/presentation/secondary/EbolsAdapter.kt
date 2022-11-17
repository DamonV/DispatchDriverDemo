package com.driver.reliantdispatch.presentation.secondary

import com.driver.reliantdispatch.domain.entities.joined.EbolJoined

interface EbolsAdapter {
    var mDataset: MutableList<EbolJoined>

    fun notifyDataSetChanged()
}