package com.driver.reliantdispatch.data

import com.driver.reliantdispatch.data.dto.VinResponseDTO
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

const val VIN_URL = "http://specifications.vinaudit.com/"
const val VIN_API_KEY = "6GZ5554ROZHOU7P"

class VinRetrofitService{

    private val vinaudit by lazy {
        Retrofit.Builder()
            .baseUrl(VIN_URL)
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getSpecifications by lazy {
        vinaudit.create(GetSpecifications::class.java)
    }

    interface GetSpecifications {
        @POST("getspecifications.php")
        @FormUrlEncoded
        fun queryAsync(
            @Field("vin") vin: String,
            @Field("key") key: String,
            @Field("format") format: String
        ): Deferred<Response<VinResponseDTO>>
    }
}