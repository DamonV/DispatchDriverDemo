package com.driver.reliantdispatch.domain

import android.util.Log
import com.driver.reliantdispatch.App
import com.driver.reliantdispatch.domain.boundaries.ApiGateway
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.dto.LoginResponseDTO
import com.driver.reliantdispatch.domain.entities.Profile


class AuthUseCase {

    private val api: ApiGateway = App.component.getApiGateway()
    private val global = App.component.getGlobal()

    suspend fun login(login: String, password: String, rememberMe: Boolean): ApiResponseDTO {
        global.isCleanLogin = global.mToken.isNullOrEmpty()
        val response = ApiResponseDTO(
            body = LoginResponseDTO(
                "sdlfklj1234",
                86400
            ),
            success = true
        )
        //api.login(login, password)
        if (response.success) with (response.body as LoginResponseDTO){
            global.mToken = token
            global.setExpiresAt(expires_in)
            global.mRememberMe = rememberMe
            global.mUser = login
            obtainProfile()
        }
        return response
    }

    suspend fun forgot(email: String) = api.forgot(email)

    suspend fun refresh(): ApiResponseDTO {
        Log.d(LOG_TAG, "refresh in domain layer")
        val response = api.refresh()
        if (response.success) obtainProfile()
        return response
    }

    suspend fun logout() = api.logout()

    suspend fun obtainProfile() {
        val responseProfile = api.getProfile()
        if (responseProfile.success){
            global.mProfile.postValue(responseProfile.body as Profile)
        }
    }

}