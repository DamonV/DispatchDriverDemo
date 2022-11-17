package com.driver.reliantdispatch.data.dto

import com.google.gson.annotations.SerializedName

data class DamageMarkDTO(

    @SerializedName("type")
    var damageKind: Int,          // index in "damage kind constant array"

    //var typeInsp: Int,                 // pickup / delivery

    var x: Int,                   //    x - coordinate in 0..width pixels of image

    var y: Int                    //    y - coordinate in 0..height pixels of image
)