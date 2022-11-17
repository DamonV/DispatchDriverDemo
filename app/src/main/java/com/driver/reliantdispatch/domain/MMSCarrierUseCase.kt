package com.driver.reliantdispatch.domain

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.ApiGateway


class MMSCarrierUseCase {

    private val api: ApiGateway = App.component.getApiGateway()

    suspend fun getCarrierList() = api.getCarrierList()

    suspend fun testSMS(carrierId: Int, phone: String) = api.testSMS(carrierId, phone)
}