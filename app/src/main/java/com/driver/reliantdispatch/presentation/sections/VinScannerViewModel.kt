package com.driver.reliantdispatch.presentation.sections

import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.presentation.dto.EventType
import com.driver.reliantdispatch.presentation.dto.PresentationEvent


class VinScannerViewModel : SectionViewModel(){

    var mOrientationInd: Int = 0

    fun onEnterVinClick(){
        viewModelEvent.value = PresentationEvent(
            EventType.NAVIGATE,
            R.id.nav_vehicle_info
        )
    }

}