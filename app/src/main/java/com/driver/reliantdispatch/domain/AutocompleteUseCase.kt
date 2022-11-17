package com.driver.reliantdispatch.domain

import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO

class AutocompleteUseCase {

    private val api: ApiGateway = App.component.getApiGateway()

    suspend fun autocompleteZip(value: String): ApiResponseDTO = api.autocompleteZip(value, true)

    suspend fun autocompleteCity(value: String): ApiResponseDTO = api.autocompleteZip(value, false)
}