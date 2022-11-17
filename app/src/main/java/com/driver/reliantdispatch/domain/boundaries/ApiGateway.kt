package com.driver.reliantdispatch.domain.boundaries

import android.location.Location
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.driver.reliantdispatch.data.EbolsDataSourceFactory
import com.driver.reliantdispatch.domain.dto.ApiResponseDTO
import com.driver.reliantdispatch.domain.entities.EbolStatus
import com.driver.reliantdispatch.domain.entities.Profile
import com.driver.reliantdispatch.domain.entities.joined.EbolJoined


interface ApiGateway {

    suspend fun postEbol(ebol: EbolJoined): ApiResponseDTO

    suspend fun performInspection(ebol: EbolJoined): ApiResponseDTO

    fun getEbolsPaged(status: EbolStatus, addResult: MutableLiveData<ApiResponseDTO>): EbolsDataSourceFactory

    suspend fun getEbols(status: EbolStatus, page: Int = 1, perPage: Int = 100, sortField: String = "id", sortType: String = "DESC"): ApiResponseDTO

    suspend fun login(login: String, password: String): ApiResponseDTO

    suspend fun forgot(email: String): ApiResponseDTO

    suspend fun logout(): ApiResponseDTO

    suspend fun refresh(): ApiResponseDTO

    suspend fun autocompleteZip(value: String, byZip: Boolean): ApiResponseDTO

    suspend fun vehicleType(): ApiResponseDTO

    suspend fun getCounters(): ApiResponseDTO

    suspend fun ebolPaid(id: Long?): ApiResponseDTO

    suspend fun ebolCancel(id: Long, restore: Boolean = false): ApiResponseDTO

    suspend fun ebolArchive(id: Long, restore: Boolean = false): ApiResponseDTO

    suspend fun ebolDelete(id: Long): ApiResponseDTO

    suspend fun getProfile(): ApiResponseDTO

    suspend fun putProfile(profile: Profile): ApiResponseDTO

    suspend fun changePassword(password: String, newPassword: String): ApiResponseDTO

    suspend fun getFile(filename: String, extension: String): Uri?

    suspend fun getCarrierList(): ApiResponseDTO

    suspend fun testSMS(carrierId: Int, phone: String): ApiResponseDTO

    suspend fun putGPS(location: Location): ApiResponseDTO
}