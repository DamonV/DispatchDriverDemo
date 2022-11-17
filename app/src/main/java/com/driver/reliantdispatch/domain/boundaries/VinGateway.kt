package com.driver.reliantdispatch.domain.boundaries

import com.driver.reliantdispatch.domain.entities.Vehicle

interface VinGateway {
    suspend fun decodeVin(vin: String): Vehicle?
}