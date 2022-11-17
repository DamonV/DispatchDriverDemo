package com.driver.reliantdispatch.domain.entities.joined

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.R
import com.driver.reliantdispatch.domain.boundaries.getSafe
import com.driver.reliantdispatch.domain.entities.DamageMark
import com.driver.reliantdispatch.domain.entities.InspectImage

data class InspectImageJoined(

    @Embedded
    val inspectImage: InspectImage?,

    @Relation(parentColumn = "id", entityColumn = "inspectImageId")
    var damagesList: MutableList<DamageMark> = mutableListOf()

){
    @Ignore
    var apiId: Long? = null

    @Ignore
    var posted: Boolean = false


    companion object {
        @JvmStatic
        fun damagesString(item: InspectImageJoined?): String {
            val context = App.component.getContext()
            var result = context.getString(R.string.text_no_damages)
            if (item != null && item.damagesList.size > 0){
                result = context.resources.getQuantityString(R.plurals.text_damages, item.damagesList.size, item.damagesList.size)+" "
                val damageGroups = item.damagesList.groupBy {it.damageKind}
                val damageKindsArr = App.component.getResourcesGateway().damageKindsShortArray
                for (group in damageGroups){
                    result += "${damageKindsArr.getSafe(group.key)} (${group.value.size}), "
                }
                result = result.substring(0, result.length - 2) //if (result.endsWith(", "))
            }
            return result
        }
    }
}