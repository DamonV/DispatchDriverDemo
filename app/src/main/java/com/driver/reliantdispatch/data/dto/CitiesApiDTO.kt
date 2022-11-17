package com.driver.reliantdispatch.data.dto



data class CitiesApiDTO(
    val result: String,
    val cities: Array<CityApiDTO>?
)